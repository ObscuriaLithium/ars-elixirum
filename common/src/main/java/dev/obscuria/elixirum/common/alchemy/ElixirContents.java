package dev.obscuria.elixirum.common.alchemy;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ElixirContents(int color, ImmutableList<ElixirEffect> effects) {
    public static final Codec<ElixirContents> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirContents> STREAM_CODEC;
    public static final ElixirContents WATER = new ElixirContents(Elixirum.WATER_COLOR, ImmutableList.of());

    public static ElixirContents create(int color, List<ElixirEffect> effects) {
        return new ElixirContents(color, ImmutableList.copyOf(effects));
    }

    public static ElixirContents create(int color, ElixirEffect... effects) {
        return new ElixirContents(color, ImmutableList.copyOf(effects));
    }

    public static ElixirContents get(ItemStack stack) {
        return stack.getOrDefault(ElixirumDataComponents.ELIXIR_CONTENTS.value(), WATER);
    }

    public static int getOverlayColor(ItemStack stack, int layer) {
        return layer != 1 ? -1 : FastColor.ARGB32.opaque(ElixirContents.get(stack).color());
    }

    public boolean isEmpty() {
        return this.effects.isEmpty();
    }

    public boolean hasInstantEffects() {
        return effects.stream().anyMatch(ElixirEffect::isInstantenous);
    }

    public ElixirContents withColor(int color) {
        return create(color, this.effects);
    }

    public ElixirContents split(int count) {
        return this.scale(1.0 / count);
    }

    public ElixirContents scale(double scale) {
        return ElixirContents.create(this.color, scale != 1.0
                ? effects().stream().map(effect -> effect.scale(scale)).toList()
                : effects());
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("color").forGetter(ElixirContents::color),
                ElixirEffect.CODEC.listOf().fieldOf("effects").forGetter(ElixirContents::effects)
        ).apply(instance, ElixirContents::create));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ElixirContents::color,
                ElixirEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), ElixirContents::effects,
                ElixirContents::create);
    }
}
