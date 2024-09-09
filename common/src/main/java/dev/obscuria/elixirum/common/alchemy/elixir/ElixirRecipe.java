package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

import java.util.List;

public record ElixirRecipe(List<Item> ingredients) {
    public static final Codec<ElixirRecipe> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirRecipe> STREAM_CODEC;

    static {
        CODEC = BuiltInRegistries.ITEM.byNameCodec().listOf()
                .xmap(ElixirRecipe::new, ElixirRecipe::ingredients);
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list()),
                ElixirRecipe::ingredients, ElixirRecipe::new);
    }
}
