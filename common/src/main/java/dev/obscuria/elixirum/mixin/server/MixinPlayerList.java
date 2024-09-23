package dev.obscuria.elixirum.mixin.server;

import dev.obscuria.elixirum.server.hooks.PlayerListHooks;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method = "placeNewPlayer", at = @At(value = "TAIL"))
    private void placeNewPlayer_Listener(Connection connection, ServerPlayer player,
                                         CommonListenerCookie cookie, CallbackInfo ci) {

        PlayerListHooks.playerJoined(player);
    }

    @Inject(method = "remove", at = @At(value = "TAIL"))
    private void remove_Listener(ServerPlayer player, CallbackInfo ci) {

        PlayerListHooks.playerLeaved(player);
    }
}
