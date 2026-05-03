package dev.obscuria.elixirum.common.hooks;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.common.world.tooltip.AlchemyPropertiesTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class _ItemStackHooks {

    public static @Nullable TooltipComponent injectTooltipImage(ItemStack stack) {
        if (!ArsElixirumAPI.shouldDisplayComposition()) return null;
        final var properties = ClientAlchemy.INSTANCE.ingredients().propertiesOf(stack);
        if (properties.isEmpty()) return null;
        return new AlchemyPropertiesTooltip(stack, properties);
    }
}
