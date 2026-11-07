package dev.obscuria.elixirum.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;

public final class ItemStackMigration {
    public static ItemStack read(HolderLookup.Provider registries, CompoundTag tag) {
        if (tag.isEmpty()) return ItemStack.EMPTY;
        if (tag.contains("Count")) {
            if (!tag.contains("Count", Tag.TAG_ANY_NUMERIC)) throw new IllegalArgumentException("Invalid legacy potion shelf item count");
            if (tag.getByte("Count") <= 0 || tag.getString("id").equals("minecraft:air")) return ItemStack.EMPTY;
        }

        if (tag.contains("Count")) tag = (CompoundTag) DataFixers.getDataFixer().update(References.ITEM_STACK,
                new Dynamic<>(NbtOps.INSTANCE, tag), 3465, SharedConstants.getCurrentVersion().getDataVersion().getVersion()).getValue();
        return ItemStack.parse(registries, tag).orElseThrow(() -> new IllegalArgumentException("Invalid potion shelf item stack"));
    }
}
