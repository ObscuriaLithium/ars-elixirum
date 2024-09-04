package dev.obscuria.elixirum.common.alchemy;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record ElixirData(ImmutableList<ElixirEffect> effects) {
    public static final Codec<ElixirData> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirData> STREAM_CODEC;
    public static final ElixirData EMPTY = new ElixirData(ImmutableList.of());

    public static ElixirData create(List<ElixirEffect> effects) {
        return new ElixirData(ImmutableList.copyOf(effects));
    }

    public static ElixirData create(ElixirEffect... effects) {
        return new ElixirData(ImmutableList.copyOf(effects));
    }

    public boolean isEmpty() {
        return this.effects.isEmpty();
    }

    public boolean hasInstantEffects() {
        return effects.stream().anyMatch(ElixirEffect::isInstantenous);
    }

    public ElixirData split(int count) {
        return this.scale(1.0 / count);
    }

    public ElixirData scale(double scale) {
        return ElixirData.create(scale != 1.0
                ? effects().stream().map(effect -> effect.scale(scale)).toList()
                : effects());
    }

    private List<ElixirEffect> toMutableList() {
        return new ArrayList<>(effects());
    }

    static {
        CODEC = ElixirEffect.CODEC.listOf().xmap(ElixirData::create, ElixirData::toMutableList);
        STREAM_CODEC = StreamCodec.composite(
                ElixirEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), ElixirData::toMutableList,
                ElixirData::create);
    }
}
