package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.server.ServerAlchemy;
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

    @Inject(method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;initInventoryMenu()V",
                    shift = At.Shift.AFTER,
                    ordinal = 0))
    private void placeNewPlayer$alchemyHook(Connection connection, ServerPlayer player,
                                            CommonListenerCookie cookie, CallbackInfo ci) {
        ServerAlchemy.whenPlayerLoggedIn(player);
    }
}
