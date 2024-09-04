package dev.obscuria.elixirum.common.alchemy.properties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record AlchemyProperties(ImmutableList<EssenceStack> essences) implements TooltipProvider {
    public static final Codec<AlchemyProperties> DIRECT_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyProperties> STREAM_CODEC;
    public static final AlchemyProperties EMPTY = new AlchemyProperties(ImmutableList.of());

    public static AlchemyProperties create(List<EssenceStack> essences) {
        return new AlchemyProperties(ImmutableList.copyOf(essences));
    }

    public static AlchemyProperties create(EssenceStack... essences) {
        return new AlchemyProperties(ImmutableList.copyOf(essences));
    }

    public static AlchemyProperties single(Holder<Essence> essence, int weight) {
        return new AlchemyProperties(ImmutableList.of(new EssenceStack(essence, weight)));
    }

    public boolean isEmpty() {
        return this.essences.isEmpty();
    }

    public boolean contains(Holder<Essence> essence) {
        return this.essences.stream()
                .anyMatch(stack -> stack.essenceHolder().equals(essence));
    }

    public boolean contains(Holder<Essence> essence, int weight) {
        return this.essences.stream()
                .anyMatch(stack -> stack.essenceHolder().equals(essence) && stack.weight() == weight);
    }

    public int getWeight(Holder<Essence> essence) {
        for (var stack : this.essences)
            if (stack.essenceHolder().equals(essence))
                return stack.weight();
        return 0;
    }

    public AlchemyProperties with(Holder<Essence> essence, int weight) {
        var result = Lists.<EssenceStack>newArrayList();
        for (var stack : essences()) {
            if (stack.essenceHolder().equals(essence)) continue;
            result.add(stack);
        }
        result.add(new EssenceStack(essence, weight));
        return create(result);
    }

    public AlchemyProperties exclude(Holder<Essence> essence) {
        var result = Lists.<EssenceStack>newArrayList();
        for (var stack : essences()) {
            if (stack.essenceHolder().equals(essence)) continue;
            result.add(stack);
        }
        return create(result);
    }

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        if (isEmpty()) return;
        consumer.accept(Component.literal("Essences:").withStyle(ChatFormatting.GRAY));
        this.essences().forEach(essence ->
                consumer.accept(Component.literal(" ")
                        .append(Component.literal("x" + essence.weight()))
                        .append(Component.literal(" "))
                        .append(essence.getEssence().getName())
                        .withStyle(ChatFormatting.LIGHT_PURPLE)));
    }

    private static AlchemyProperties byMap(Map<Holder<Essence>, Integer> map) {
        return AlchemyProperties.create(map.entrySet().stream()
                .map(entry -> new EssenceStack(entry.getKey(), entry.getValue()))
                .toList());
    }

    private Map<Holder<Essence>, Integer> toMap() {
        var map = Maps.<Holder<Essence>, Integer>newHashMap();
        this.essences.forEach(stack -> map.put(stack.essenceHolder(), stack.weight()));
        return map;
    }

    static {
        DIRECT_CODEC = Codec
                .unboundedMap(Essence.CODEC, Codec.INT).stable()
                .xmap(AlchemyProperties::byMap, AlchemyProperties::toMap);
        STREAM_CODEC = StreamCodec.composite(
                EssenceStack.STREAM_CODEC.apply(ByteBufCodecs.list()), AlchemyProperties::essences,
                AlchemyProperties::create);
    }
}
