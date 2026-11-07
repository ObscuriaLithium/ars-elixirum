package dev.obscuria.elixirum.helpers;

import dev.obscuria.elixirum.api.alchemy.components.StyleVariant;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import dev.obscuria.elixirum.common.world.NBTComponents;
import net.minecraft.world.item.ItemStack;

public final class StyleHelper {

    public static StyleVariant style(ItemStack stack) {
        return ItemStackCache.of(stack).style();
    }

    public static void setStyle(ItemStack stack, StyleVariant value) {
        NBTComponents.STYLE.write(stack.getOrCreateTag(), value);
    }

    public static void setStyleIfNotDefault(ItemStack stack, StyleVariant value) {
        if (value.isDefault()) return;
        setStyle(stack, value);
    }

    public static Chroma chroma(ItemStack stack) {
        return ItemStackCache.of(stack).chroma();
    }

    public static void setChroma(ItemStack stack, Chroma value) {
        NBTComponents.CHROMA.write(stack.getOrCreateTag(), value);
    }

    public static void setChromaIfNotDefault(ItemStack stack, Chroma value) {
        if (value.isDefault()) return;
        setChroma(stack, value);
    }
}