package dev.obscuria.elixirum.platform;

import dev.obscuria.elixirum.registry.LazyRegister;
import net.minecraft.server.level.ServerPlayer;

public interface IPlatform {

    <TValue> void register(LazyRegister<TValue> registrar);

    void sendToServer(Object packet);

    void sendToClient(ServerPlayer player, Object message);

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }
}