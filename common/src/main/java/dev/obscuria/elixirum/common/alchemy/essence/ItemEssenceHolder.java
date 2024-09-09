package dev.obscuria.elixirum.common.alchemy.essence;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ItemEssenceHolder implements TooltipProvider {
    public static final Codec<ItemEssenceHolder> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEssenceHolder> STREAM_CODEC;
    public static final ItemEssenceHolder EMPTY = new ItemEssenceHolder(Map.of());
    private final Object2IntMap<ResourceLocation> essences;

    public static ItemEssenceHolder create(Map<ResourceLocation, Integer> essences) {
        return new ItemEssenceHolder(essences);
    }

    public static ItemEssenceHolder single(Holder.Reference<Essence> essence, int weight) {
        return new ItemEssenceHolder(Map.of(essence.key().location(), weight));
    }

    private ItemEssenceHolder(Map<ResourceLocation, Integer> essences) {
        this.essences = new Object2IntOpenHashMap<>(essences);
    }

    public boolean isEmpty() {
        return this.essences.isEmpty();
    }

    public Object2IntOpenHashMap<ResourceLocation> getEssences() {
        return new Object2IntOpenHashMap<>(this.essences);
    }

    public Object2IntOpenHashMap<Holder<Essence>> getEssences(HolderLookup.Provider lookup) {
        final var registry = lookup.lookupOrThrow(ElixirumRegistries.ESSENCE);
        final var result = new Object2IntOpenHashMap<Holder<Essence>>();
        for (var entry : this.essences.object2IntEntrySet()) {
            final var essence = registry.getOrThrow(Essence.key(entry.getKey()));
            result.put(essence, entry.getIntValue());
        }
        return result;
    }

    public boolean contains(ResourceLocation essence) {
        return this.essences.containsKey(essence);
    }

    public int getWeight(ResourceLocation essence) {
        return this.essences.getOrDefault(essence, 0);
    }

    public ItemEssenceHolder with(ResourceLocation essence, int weight) {
        var result = this.getEssences();
        result.put(essence, weight);
        return create(result);
    }

    public ItemEssenceHolder exclude(Holder<Essence> essence) {
        var result = this.getEssences();
        result.removeInt(essence);
        return create(result);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag) {
        if (isEmpty()) return;
        final var lookup = context.registries();
        if (lookup == null) return;
        consumer.accept(Component.literal("Essences:").withStyle(ChatFormatting.GRAY));
        this.getEssences(lookup).object2IntEntrySet().forEach(entry ->
                consumer.accept(Component.literal(" ")
                        .append(Component.literal("x" + entry.getIntValue()))
                        .append(Component.literal(" "))
                        .append(entry.getKey().value().getName())
                        .withStyle(ChatFormatting.LIGHT_PURPLE)));
    }

    static {
        CODEC = Codec
                .unboundedMap(ResourceLocation.CODEC, Codec.INT)
                .xmap(ItemEssenceHolder::create, ItemEssenceHolder::getEssences);
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT), ItemEssenceHolder::getEssences,
                ItemEssenceHolder::create);
    }
}
