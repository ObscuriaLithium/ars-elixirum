package dev.obscuria.elixirum.common.alchemy.essence;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class ItemEssences implements TooltipProvider {
    public static final Codec<ItemEssences> DIRECT_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemEssences> STREAM_CODEC;
    public static final ItemEssences EMPTY = new ItemEssences(Map.of());
    private final Object2IntMap<Holder<Essence>> essences;

    public static ItemEssences create(Map<Holder<Essence>, Integer> essences) {
        return new ItemEssences(essences);
    }

    public static ItemEssences single(Holder<Essence> essence, int weight) {
        return new ItemEssences(Map.of(essence, weight));
    }

    private ItemEssences(Map<Holder<Essence>, Integer> essences) {
        this.essences = new Object2IntOpenHashMap<>(essences);
    }

    public boolean isEmpty() {
        return this.essences.isEmpty();
    }

    public Object2IntMap<Holder<Essence>> getEssences() {
        return new Object2IntOpenHashMap<>(this.essences);
    }

    public boolean contains(Holder<Essence> essence) {
        return this.essences.containsKey(essence);
    }

    public int getWeight(Holder<Essence> essence) {
        return this.essences.getOrDefault(essence, 0);
    }

    public ItemEssences with(Holder<Essence> essence, int weight) {
        var result = this.getEssences();
        result.put(essence, weight);
        return create(result);
    }

    public ItemEssences exclude(Holder<Essence> essence) {
        var result = this.getEssences();
        result.removeInt(essence);
        return create(result);
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        if (isEmpty()) return;
        consumer.accept(Component.literal("Essences:").withStyle(ChatFormatting.GRAY));
        this.essences.object2IntEntrySet().forEach(entry ->
                consumer.accept(Component.literal(" ")
                        .append(Component.literal("x" + entry.getIntValue()))
                        .append(Component.literal(" "))
                        .append(entry.getKey().value().getName())
                        .withStyle(ChatFormatting.LIGHT_PURPLE)));
    }

    static {
        DIRECT_CODEC = Codec
                .unboundedMap(Essence.CODEC, Codec.INT).stable()
                .xmap(ItemEssences::create, ItemEssences::getEssences);
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, Essence.STREAM_CODEC, ByteBufCodecs.INT), ItemEssences::getEssences,
                ItemEssences::create);
    }
}
