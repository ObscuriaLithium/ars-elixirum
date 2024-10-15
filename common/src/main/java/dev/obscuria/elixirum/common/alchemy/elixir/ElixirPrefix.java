package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

public record ElixirPrefix(Optional<Holder<MobEffect>> source, String key)
{
    public static final Codec<ElixirPrefix> DIRECT_CODEC;
    public static final Codec<Holder<ElixirPrefix>> CODEC;

    static
    {
        DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MobEffect.CODEC.optionalFieldOf("source").forGetter(ElixirPrefix::source),
                Codec.STRING.fieldOf("key").forGetter(ElixirPrefix::key)
        ).apply(instance, ElixirPrefix::new));
        CODEC = RegistryFixedCodec.create(ElixirumRegistries.ELIXIR_PREFIX);
    }
}
