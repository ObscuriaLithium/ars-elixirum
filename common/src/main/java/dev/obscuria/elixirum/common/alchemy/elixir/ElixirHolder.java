package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import dev.obscuria.elixirum.registry.ElixirumItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ElixirHolder(ElixirRecipe recipe,
                           ElixirStyle style,
                           Optional<Component> name,
                           Optional<Component> description) {
    public static final Codec<ElixirHolder> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirHolder> STREAM_CODEC;

    public ItemStack createStack(HolderGetter<Essence> getter) {
        final var stack = ElixirumItems.ELIXIR.value().getDefaultInstance();
        final var mixer = new ElixirMixer(recipe);
        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS.value(), mixer.getResult(getter));
        stack.set(ElixirumDataComponents.ELIXIR_STYLE.value(), this.style);
        return stack;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ElixirRecipe.CODEC.fieldOf("recipe").forGetter(ElixirHolder::recipe),
                ElixirStyle.CODEC.fieldOf("style").forGetter(ElixirHolder::style),
                ComponentSerialization.FLAT_CODEC.optionalFieldOf("name").forGetter(ElixirHolder::name),
                ComponentSerialization.FLAT_CODEC.optionalFieldOf("description").forGetter(ElixirHolder::description)
        ).apply(instance, ElixirHolder::new));
        STREAM_CODEC = StreamCodec.composite(
                ElixirRecipe.STREAM_CODEC, ElixirHolder::recipe,
                ElixirStyle.STREAM_CODEC, ElixirHolder::style,
                ComponentSerialization.OPTIONAL_STREAM_CODEC, ElixirHolder::name,
                ComponentSerialization.OPTIONAL_STREAM_CODEC, ElixirHolder::description,
                ElixirHolder::new);
    }
}
