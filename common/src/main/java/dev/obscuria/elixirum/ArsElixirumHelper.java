package dev.obscuria.elixirum;

import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.basics.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import dev.obscuria.elixirum.common.world.NBTComponents;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class ArsElixirumHelper {

    public static boolean shouldShowComposition() {
        var server = FragmentumProxy.optionalServer();
        if (server.isPresent() && server.get() instanceof DedicatedServer) return false;
        @Nullable var player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ElixirumItems.ALCHEMIST_EYE.asItem());
    }

    public static ElixirContents getElixirContents(ItemStack stack) {
        return ItemStackCache.of(stack).elixirContents();
    }

    public static void setElixirContents(ItemStack stack, ElixirContents value) {
        NBTComponents.ELIXIR_CONTENTS.write(stack.getOrCreateTag(), value);
    }

    public static ExtractContents getExtractContents(ItemStack stack) {
        return ItemStackCache.of(stack).extractContents();
    }

    public static void setExtractContents(ItemStack stack, ExtractContents value) {
        NBTComponents.EXTRACT_CONTENTS.write(stack.getOrCreateTag(), value);
    }

    public static StyleVariant getStyle(ItemStack stack) {
        return ItemStackCache.of(stack).style();
    }

    public static void setStyle(ItemStack stack, StyleVariant value) {
        NBTComponents.STYLE.write(stack.getOrCreateTag(), value);
    }

    public static Chroma getChroma(ItemStack stack) {
        return ItemStackCache.of(stack).chroma();
    }

    public static void setChroma(ItemStack stack, Chroma value) {
        NBTComponents.CHROMA.write(stack.getOrCreateTag(), value);
    }

    public static double amplifierFactor(double temper) {
        return Mth.clampedMap(temper, -1f, 1f, 0.1f, 1f);
    }

    public static double durationFactor(double temper) {
        return Mth.clampedMap(temper, 1f, -1f, 0.1f, 1f);
    }
}
