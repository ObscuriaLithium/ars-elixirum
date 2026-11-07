package dev.obscuria.elixirum.server;

import dev.obscuria.elixirum.common.network.ServerboundRecipeRemoveRequest;
import dev.obscuria.elixirum.common.network.ServerboundRecipeSaveRequest;
import dev.obscuria.elixirum.common.network.ServerboundSetChromaRequest;
import dev.obscuria.elixirum.common.network.ServerboundSetStyleRequest;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import net.minecraft.server.level.ServerPlayer;

public final class ServerPayloadListener {

    public static void handle(ServerPlayer player, ServerboundRecipeSaveRequest request) {
        if (player.getServer() == null) return;
        var profile = ServerAlchemy.get(player.getServer()).profileOf(player);
        profile.recipeCollection().save(request.configuredRecipe());
    }

    public static void handle(ServerPlayer player, ServerboundRecipeRemoveRequest request) {
        if (player.getServer() == null) return;
        var profile = ServerAlchemy.get(player.getServer()).profileOf(player);
        profile.recipeCollection().remove(request.recipeUid());
    }

    public static void handle(ServerPlayer player, ServerboundSetStyleRequest request) {
        if (player.getServer() == null) return;
        var profile = ServerAlchemy.get(player.getServer()).profileOf(player);
        profile.recipeCollection().setStyle(request.recipeUid(), request.style());
    }

    public static void handle(ServerPlayer player, ServerboundSetChromaRequest request) {
        if (player.getServer() == null) return;
        var profile = ServerAlchemy.get(player.getServer()).profileOf(player);
        profile.recipeCollection().setChroma(request.recipeUid(), request.chroma());
    }
}
