package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.fragmentum.world.tooltip.TooltipOptions;
import dev.obscuria.fragmentum.world.tooltip.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HoneySolventItem extends Item {

    public HoneySolventItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        var tooltip = Component.translatable("tooltip.elixirum.honey_solvent");
        components.addAll(Tooltips.process(tooltip, true, TooltipOptions.DESCRIPTION));
    }
}
