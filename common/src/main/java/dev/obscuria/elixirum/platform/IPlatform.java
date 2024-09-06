package dev.obscuria.elixirum.platform;

import dev.obscuria.elixirum.registry.LazyRegister;
import net.minecraft.server.level.ServerPlayer;

public interface IPlatform {

    <TValue> void register(LazyRegister<TValue> registrar);

    void sendToServer(Object packet);

    void sendToPlayer(ServerPlayer player, Object packet);

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    boolean isClient();
}