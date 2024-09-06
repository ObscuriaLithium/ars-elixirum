package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Inject(method = "saveEverything",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;saveAll()V",
                    shift = At.Shift.AFTER))
    private void saveEverything$alchemyHook(boolean suppressLog, boolean flush, boolean forced,
                                            CallbackInfoReturnable<Boolean> info) {
        ServerAlchemy.whenServerSaved((MinecraftServer) (Object) this);
    }

    @Inject(method = "lambda$reloadResources$30",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;reloadResources()V",
                    shift = At.Shift.AFTER))
    private void reloadResources$alchemyHook(Collection<?> selectedIds,
                                             MinecraftServer.ReloadableResources resources,
                                             CallbackInfo info) {
        ServerAlchemy.whenResourcesReloaded((MinecraftServer) (Object) this);
    }
}
