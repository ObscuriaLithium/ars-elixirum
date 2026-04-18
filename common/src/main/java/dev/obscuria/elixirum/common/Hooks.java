package dev.obscuria.elixirum.common;

import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.common.world.tooltip.CompositionTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class Hooks {

    public static @Nullable TooltipComponent injectTooltipImage(ItemStack stack) {
        if (!ArsElixirumHelper.shouldShowComposition()) return null;
        final var properties = ClientAlchemy.INSTANCE.ingredients().propertiesOf(stack);
        if (properties.isEmpty()) return null;
        return new CompositionTooltip(stack, properties, properties.aspect());
    }
}