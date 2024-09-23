package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record ElixirRecipe(@Unmodifiable List<Item> ingredients) {
    public static final Codec<ElixirRecipe> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirRecipe> STREAM_CODEC;
    public static final ElixirRecipe EMPTY = new ElixirRecipe(List.of());

    public boolean isEmpty() {
        return ingredients.isEmpty();
    }

    public ElixirRecipe with(Item item) {
        return new ElixirRecipe(Util.make(Lists.newArrayList(), list -> {
            list.addAll(this.ingredients);
            list.add(item);
        }));
    }

    static {
        CODEC = BuiltInRegistries.ITEM.byNameCodec().listOf()
                .xmap(ElixirRecipe::new, ElixirRecipe::ingredients);
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list()),
                ElixirRecipe::ingredients, ElixirRecipe::new);
    }
}
