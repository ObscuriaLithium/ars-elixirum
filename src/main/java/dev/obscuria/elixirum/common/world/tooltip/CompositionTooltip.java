package dev.obscuria.elixirum.common.world.tooltip;

import dev.obscuria.elixirum.api.alchemy.EssenceProvider;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record CompositionTooltip(
        ItemStack stack,
        EssenceProvider provider,
        Aspect aspect
) implements TooltipComponent {}
