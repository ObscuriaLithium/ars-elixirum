package dev.obscuria.elixirum.mixin;

import com.mojang.datafixers.DataFixer;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import dev.obscuria.elixirum.server.ServerExtensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public final class MixinMinecraftServer implements ServerExtensions {

    private @Unique @Nullable ServerAlchemy elixirum$alchemy;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(Thread thread, LevelStorageSource.LevelStorageAccess storageSource,
                      PackRepository packRepository, WorldStem worldStem, Proxy proxy,
                      DataFixer fixerUpper, Services services, ChunkProgressListenerFactory factory,
                      CallbackInfo callbackInfo) {
        var server = (MinecraftServer) (Object) this;
        this.elixirum$alchemy = new ServerAlchemy(server);
    }

    @Override
    public ServerAlchemy elixirum$getAlchemy() {
        assert elixirum$alchemy != null;
        return elixirum$alchemy;
    }
}
