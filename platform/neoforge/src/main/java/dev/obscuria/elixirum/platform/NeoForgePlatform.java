package dev.obscuria.elixirum.platform;

import dev.obscuria.elixirum.registry.LazyRegister;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class NeoForgePlatform implements IPlatform {

    @Override
    public <TValue> void register(LazyRegister<TValue> registrar) {

    }

    @Override
    public void sendToServer(Object packet) {

    }

    @Override
    public void sendToClient(ServerPlayer player, Object message) {

    }

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
}