package dev.obscuria.elixirum.helpers;

import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.basics.ExtractContents;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import dev.obscuria.elixirum.common.world.NBTComponents;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class ContentsHelper {

    public static boolean shouldDisplayComposition() {
        var server = FragmentumProxy.optionalServer();
        if (server.isPresent() && server.get() instanceof DedicatedServer) return false;
        @Nullable var player = Minecraft.getInstance().player;
        if (player == null) return false;
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ElixirumItems.ALCHEMIST_EYE.asItem());
    }

    public static ElixirContents elixir(ItemStack stack) {
        return ItemStackCache.of(stack).elixirContents();
    }

    public static void setElixir(ItemStack stack, ElixirContents value) {
        NBTComponents.ELIXIR_CONTENTS.write(stack.getOrCreateTag(), value);
    }

    public static ExtractContents extract(ItemStack stack) {
        return ItemStackCache.of(stack).extractContents();
    }

    public static void setExtract(ItemStack stack, ExtractContents value) {
        NBTComponents.EXTRACT_CONTENTS.write(stack.getOrCreateTag(), value);
    }
}
