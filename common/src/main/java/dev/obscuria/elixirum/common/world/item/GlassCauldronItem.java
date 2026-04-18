package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.fragmentum.world.tooltip.TooltipOptions;
import dev.obscuria.fragmentum.world.tooltip.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlassCauldronItem extends BlockItem {

    public GlassCauldronItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        components.addAll(Tooltips.process(
                Component.translatable("tooltip.elixirum.glass_cauldron"),
                TooltipOptions.DEFAULT));
    }
}
