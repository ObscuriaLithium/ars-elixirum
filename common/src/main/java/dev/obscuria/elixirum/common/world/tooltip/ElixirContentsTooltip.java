package dev.obscuria.elixirum.common.world.tooltip;

import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ElixirContentsTooltip(
        ItemStack stack,
        ElixirContents contents
) implements TooltipComponent {}
