package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ElixirPage(ElixirRecipe recipe,
                         ElixirStyle style,
                         Optional<Component> name,
                         Optional<Component> description) {
    public static final Codec<ElixirPage> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirPage> STREAM_CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ElixirRecipe.CODEC.fieldOf("recipe").forGetter(ElixirPage::recipe),
                ElixirStyle.CODEC.fieldOf("style").forGetter(ElixirPage::style),
                ComponentSerialization.FLAT_CODEC.optionalFieldOf("name").forGetter(ElixirPage::name),
                ComponentSerialization.FLAT_CODEC.optionalFieldOf("description").forGetter(ElixirPage::description)
        ).apply(instance, ElixirPage::new));
        STREAM_CODEC = StreamCodec.composite(
                ElixirRecipe.STREAM_CODEC, ElixirPage::recipe,
                ElixirStyle.STREAM_CODEC, ElixirPage::style,
                ComponentSerialization.OPTIONAL_STREAM_CODEC, ElixirPage::name,
                ComponentSerialization.OPTIONAL_STREAM_CODEC, ElixirPage::description,
                ElixirPage::new);
    }
}
