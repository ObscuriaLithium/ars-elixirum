package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.alchemy.AlchemyIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record _AlchemyIngredient(Item item) implements AlchemyIngredient {

    public static final Codec<AlchemyIngredient> CODEC;

    @Override
    public Item asItem() {
        return item;
    }

    @Override
    public ItemStack asStack() {
        return item.getDefaultInstance();
    }

    @Override
    public String toString() {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    static {
        CODEC = BuiltInRegistries.ITEM.byNameCodec().xmap(
                _AlchemyIngredient::new,
                AlchemyIngredient::asItem);
    }
}
