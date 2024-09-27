package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@EssenceBlacklist
public final class ExtractItem extends Item {

    public ExtractItem() {
        super(new Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public Component getName(ItemStack stack) {
        return ExtractContents.get(stack)
                .map(contents -> (Component) Component.literal("Extract of " + contents.getEssence().getName().getString()))
                .orElseGet(() -> super.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        ExtractContents.get(stack).ifPresent(contents -> {
            components.add(Component
                    .translatable("elixirum.extract.essence", contents.weight(), contents.getEssence().getName())
                    .withStyle(ChatFormatting.GRAY));
            contents.source().ifPresent(source ->
                    components.add(Component
                            .translatable("elixirum.extract.source", source.getDescription())
                            .withStyle(ChatFormatting.GRAY)));
        });
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(ElixirumDataComponents.EXTRACT_CONTENTS);
    }
}
