package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ConfiguredElixir(List<Variant> variants)
{
    public static final Codec<ConfiguredElixir> DIRECT_CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfiguredElixir> STREAM_CODEC;

    public static ConfiguredElixir create(Variant... variants)
    {
        return new ConfiguredElixir(List.of(variants));
    }

    public static Variant variant(Variant.Template... templates)
    {
        return new Variant(List.of(templates));
    }

    public static Variant.Template template(ResourceLocation essence, int amplifier, int duration)
    {
        return new Variant.Template(essence, amplifier, duration);
    }

    public record Variant(List<Template> templates)
    {

        public record Template(ResourceLocation essence, int amplifier, int duration)
        {

        }
    }

    static
    {
        final var templateCodec = RecordCodecBuilder.<Variant.Template>create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("essence").forGetter(Variant.Template::essence),
                Codec.INT.fieldOf("amplifier").forGetter(Variant.Template::amplifier),
                Codec.INT.fieldOf("duration").forGetter(Variant.Template::duration)
        ).apply(instance, Variant.Template::new));
        final var variantCodec = templateCodec.listOf().xmap(Variant::new, Variant::templates);
        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                variantCodec.listOf().fieldOf("variants").forGetter(ConfiguredElixir::variants)
        ).apply(instance, ConfiguredElixir::new));

        final var templateStreamCodec = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, Variant.Template::essence,
                ByteBufCodecs.INT, Variant.Template::amplifier,
                ByteBufCodecs.INT, Variant.Template::duration,
                Variant.Template::new);
        final var variantStreamCodec = StreamCodec.composite(
                templateStreamCodec.apply(ByteBufCodecs.list()), Variant::templates,
                Variant::new);
        STREAM_CODEC = StreamCodec.composite(
                variantStreamCodec.apply(ByteBufCodecs.list()), ConfiguredElixir::variants,
                ConfiguredElixir::new);
    }
}
