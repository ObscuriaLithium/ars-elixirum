package dev.obscuria.elixirum.common.alchemy.ingredient.provider;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

@FunctionalInterface
public interface IntProvider {

    int compute(Item item, RandomSource random);
}
