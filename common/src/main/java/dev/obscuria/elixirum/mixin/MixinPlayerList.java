package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public final class MixinPlayerList {

    private @Final @Shadow MinecraftServer server;

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void onPlayerJoin(Connection connection, ServerPlayer player, CallbackInfo info) {
        ServerAlchemy.get(server).onPlayerJoin(player);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerLeave(ServerPlayer player, CallbackInfo info) {
        ServerAlchemy.get(server).onPlayerLeave(player);
    }
}
