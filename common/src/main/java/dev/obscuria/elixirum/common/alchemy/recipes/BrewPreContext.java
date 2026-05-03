package dev.obscuria.elixirum.common.alchemy.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record BrewPreContext(ItemStack reagent, ItemStack[] inputs) {

    private static final ThreadLocal<@Nullable BrewPreContext> SLOT = new ThreadLocal<>();

    public static void capture(NonNullList<ItemStack> items) {
        SLOT.set(new BrewPreContext(
                items.get(3).copy(),
                new ItemStack[]{
                        items.get(0).copy(),
                        items.get(1).copy(),
                        items.get(2).copy()}
        ));
    }

    public static @Nullable BrewPreContext release() {
        @Nullable var context = SLOT.get();
        SLOT.remove();
        return context;
    }
}
