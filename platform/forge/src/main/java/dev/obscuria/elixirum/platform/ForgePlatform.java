package dev.obscuria.elixirum.platform;

import dev.obscuria.elixirum.registry.LazyRegister;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatform implements IPlatform {

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

        return "Forge";
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