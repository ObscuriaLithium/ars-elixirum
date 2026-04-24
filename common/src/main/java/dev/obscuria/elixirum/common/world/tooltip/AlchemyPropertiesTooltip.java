package dev.obscuria.elixirum.common.world.tooltip;

import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record AlchemyPropertiesTooltip(
        ItemStack stack,
        AlchemyProperties properties
) implements TooltipComponent {}