package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@EssenceBlacklist
public final class ElixirItem extends Item {

    public ElixirItem() {
        super(new Properties().stacksTo(3).craftRemainder(Items.GLASS_BOTTLE));
    }

    @Override
    public Component getName(ItemStack stack) {
        var contents = stack.getOrDefault(ElixirumDataComponents.ELIXIR_CONTENTS.value(), ElixirContents.WATER);
        return !contents.isEmpty()
                ? Component.literal(getContentQuality(contents) + " Elixir of " + getContentName(contents))
                : super.getName(stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        var stack = super.getDefaultInstance();
        stack.set(ElixirumDataComponents.ELIXIR_STYLE.value(), ElixirStyle.DEFAULT);
        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS.value(), ElixirContents.WATER);
        return stack;
    }

    public static String getContentQuality(ElixirContents contents) {
        final var index = (int) Math.round(contents.effects().getFirst().getQuality() / 10.0);
        return Component.translatable("elixir.quality." + Math.clamp(index - 1, 1, 9)).getString();
    }

    public static String getContentName(ElixirContents contents) {
        return contents.effects().getFirst().getName().getString();
    }
}
