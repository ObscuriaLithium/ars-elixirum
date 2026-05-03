package dev.obscuria.elixirum.api;

import dev.obscuria.elixirum.api.alchemy.components.CustomText;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.api.alchemy.components.ExtractContents;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import dev.obscuria.elixirum.common.world.NBTComponents;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public final class ArsElixirumAPI {

    public static boolean shouldDisplayComposition() {
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

    public static Optional<CustomText> getCustomName(ItemStack stack) {
        return ItemStackCache.of(stack).customName();
    }

    public static void setCustomName(ItemStack stack, CustomText text) {
        NBTComponents.CUSTOM_NAME.write(stack.getOrCreateTag(), text);
    }

    public static Optional<CustomText> getCustomLore(ItemStack stack) {
        return ItemStackCache.of(stack).customLore();
    }

    public static void setCustomLore(ItemStack stack, CustomText text) {
        NBTComponents.CUSTOM_LORE.write(stack.getOrCreateTag(), text);
    }
}
