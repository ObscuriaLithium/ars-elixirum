package dev.obscuria.elixirum.common.alchemy.affix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirProcessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Affix(AffixType type, double modifier) {
    public static Codec<Affix> CODEC;
    public static StreamCodec<RegistryFriendlyByteBuf, Affix> STREAM_CODEC;

    public Component getDescription() {
        final var absMod = Math.abs((int) (modifier * 100.0));
        return Component.translatable(type.getDescriptionId(), (modifier < 0 ? "-" : "+") + absMod);
    }

    public void apply(ElixirProcessor processor, int index) {
        this.type.apply(this, processor, index);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AffixType.CODEC.fieldOf("type").forGetter(Affix::type),
                Codec.DOUBLE.fieldOf("modifier").forGetter(Affix::modifier)
        ).apply(instance, Affix::new));
        STREAM_CODEC = StreamCodec.composite(
                AffixType.STREAM_CODEC, Affix::type,
                ByteBufCodecs.DOUBLE, Affix::modifier,
                Affix::new);
    }
}
