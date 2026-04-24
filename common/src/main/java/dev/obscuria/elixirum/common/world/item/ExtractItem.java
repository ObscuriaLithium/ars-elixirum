package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import dev.obscuria.elixirum.common.world.tooltip.CompositionTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ExtractItem extends Item {

    public ExtractItem(Properties properties) {
        super(properties);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        var contents = ContentsHelper.extract(stack);
        if (contents.isEmpty()) return Optional.empty();
        return Optional.of(new CompositionTooltip(stack, contents, Aspect.NONE));
    }
}
