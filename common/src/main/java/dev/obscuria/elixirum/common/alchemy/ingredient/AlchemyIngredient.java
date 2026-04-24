package dev.obscuria.elixirum.common.alchemy.ingredient;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.codex.Alchemy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;

public record AlchemyIngredient(
        Either<Item, ItemStack> source
) implements ItemLike {

    public static final Codec<AlchemyIngredient> CODEC;

    public static AlchemyIngredient direct(ItemLike item) {
        return new AlchemyIngredient(Either.left(item.asItem()));
    }

    public static AlchemyIngredient dynamic(ItemStack stack) {
        return new AlchemyIngredient(Either.left(stack.getItem()));
    }

    public AlchemyProperties properties(Alchemy alchemy) {
        return alchemy.ingredients().propertiesOf(asItem());
    }

    public ItemStack asStack() {
        return source.map(Item::getDefaultInstance, Function.identity());
    }

    @Override
    public Item asItem() {
        return source.map(Function.identity(), ItemStack::getItem);
    }

    static {
        CODEC = Codec
                .either(BuiltInRegistries.ITEM.byNameCodec(), ItemStack.CODEC)
                .xmap(AlchemyIngredient::new, AlchemyIngredient::source);
    }
}
