package dev.obscuria.elixirum.common.world.item;

import dev.obscuria.fragmentum.world.tooltip.TooltipOptions;
import dev.obscuria.fragmentum.world.tooltip.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlchemistEyeItem extends ArmorItem {

    public AlchemistEyeItem(Properties properties) {
        super(ArmorMaterials.IRON, Type.HELMET, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag isAdvanced) {
        components.addAll(Tooltips.process(
                Component.translatable("tooltip.elixirum.alchemist_eye"),
                TooltipOptions.DEFAULT));
    }
}
