package dev.obscuria.elixirum.compat;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.compat.archivist.nbt.NBTComponent;
import dev.obscuria.elixirum.compat.network.PayloadCodec;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public final class ItemDataSelfCheck {

    private static final NBTComponent<Integer> CONTENTS = NBTComponent.<Integer>createBuilder()
            .path("ArsElixirum.Contents").codec(Codec.INT).build();

    public static void main(String[] args) {
        if (!ItemDataSelfCheck.class.desiredAssertionStatus()) {
            throw new IllegalStateException("Run this self-check with -ea");
        }
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        var registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        assert SharedConstants.getCurrentVersion().getDataVersion().getVersion() > 3465;
        modernRoundtrip(registries, customDataRoundtrip());
        modernRoundtrip(registries, legacyShelfStack(registries));
        payloadRoundtrip(registries);
        invalidStacks(registries);
        assert ItemStackMigration.read(registries, new CompoundTag()) == ItemStack.EMPTY;
        var legacyEmpty = new CompoundTag();
        legacyEmpty.putString("id", "minecraft:air");
        legacyEmpty.putByte("Count", (byte) 0);
        assert ItemStackMigration.read(registries, legacyEmpty).isEmpty() : "Legacy empty shelf slot was rejected";
        System.out.println("Item data compatibility self-check passed (Minecraft "
                + SharedConstants.getCurrentVersion().getName() + ")");
    }

    private static CompoundTag payload(int contents) {
        var alchemy = new CompoundTag();
        alchemy.putInt("Contents", contents);
        alchemy.putString("Sibling", "kept");
        var tag = new CompoundTag();
        tag.put("ArsElixirum", alchemy);
        tag.putString("OtherMod", "unrelated data");
        return tag;
    }

    private static ItemStack customDataRoundtrip() {
        var stack = new ItemStack(Items.GLASS_BOTTLE, 2);
        assert CONTENTS.read(stack).isEmpty();
        assert CONTENTS.readOrDefault(stack, -1) == -1;
        var initial = CustomData.of(payload(7));
        stack.set(DataComponents.CUSTOM_DATA, initial);
        CONTENTS.write(stack, 42);
        assert CONTENTS.read(stack).orElseThrow() == 42;
        assert CONTENTS.readOrDefault(stack, -1) == 42;
        assert stack.get(DataComponents.CUSTOM_DATA).copyTag().equals(payload(42)) : "Unrelated custom data changed";
        assert initial.copyTag().equals(payload(7)) : "Previously shared CustomData was mutated";
        return stack;
    }

    private static ItemStack legacyShelfStack(HolderLookup.Provider registries) {
        var legacy = new CompoundTag();
        legacy.putString("id", "minecraft:glass_bottle");
        legacy.putByte("Count", (byte) 2);
        legacy.put("tag", payload(17));
        var shelf = new CompoundTag();
        shelf.put("Slot1", legacy);
        // Exercise the helper's real ITEM_STACK data fixer, not a hand-built modern equivalent.
        var migrated = ItemStackMigration.read(registries, shelf.getCompound("Slot1"));
        assert migrated.getItem() == Items.GLASS_BOTTLE;
        assert migrated.getCount() == 2;
        assert CONTENTS.read(migrated).orElseThrow() == 17 : "DFU lost nested ArsElixirum data";
        assert migrated.get(DataComponents.CUSTOM_DATA).copyTag().equals(payload(17)) : "DFU changed unknown item tags";
        System.out.println("Actual legacy Slot1 ITEM_STACK DFU retained ArsElixirum and unrelated custom data");
        return migrated;
    }

    private static void modernRoundtrip(HolderLookup.Provider registries, ItemStack stack) {
        var saved = (CompoundTag) stack.save(registries);
        assert !saved.contains("Count") && saved.contains("components") : "Fixture is not a modern item stack";
        var restored = ItemStackMigration.read(registries, saved);
        assert ItemStack.matches(stack, restored) : "Modern item stack did not roundtrip";
        assert CONTENTS.read(restored).equals(CONTENTS.read(stack));
    }

    private static void invalidStacks(HolderLookup.Provider registries) {
        for (boolean legacy : new boolean[]{false, true}) {
            var invalid = new CompoundTag();
            invalid.putString("id", "elixirum_selfcheck:unregistered_item");
            if (legacy) invalid.putByte("Count", (byte) 1);
            else invalid.putInt("count", 1);
            try {
                ItemStackMigration.read(registries, invalid);
            } catch (IllegalArgumentException expected) {
                continue;
            }
            throw new AssertionError("Invalid nonempty " + (legacy ? "legacy" : "modern") + " item stack was accepted");
        }
    }

    private static void payloadRoundtrip(RegistryAccess registries) {
        var codec = PayloadCodec.registryFriendly(Codec.INT.fieldOf("value").codec(), () -> registries);
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            codec.write(buffer, 42);
            assert codec.read(buffer) == 42;
            assert !buffer.isReadable();
            buffer.clear();
            buffer.writeNbt(new CompoundTag());
            try {
                codec.read(buffer);
                throw new AssertionError("Invalid payload was accepted");
            } catch (IllegalStateException expected) {
                // The codec rejects a missing required value rather than decoding a partial packet.
            }
        } finally {
            buffer.release();
        }
    }
}
