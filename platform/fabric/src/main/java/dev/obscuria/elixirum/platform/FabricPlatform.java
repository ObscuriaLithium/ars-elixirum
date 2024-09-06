package dev.obscuria.elixirum.platform;

import dev.obscuria.elixirum.network.FabricClientboundItemEssencesPayload;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.registry.LazyRegister;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;

public class FabricPlatform implements IPlatform {

    @Override
    public <TValue> void register(LazyRegister<TValue> registrar) {
        registrar.register((registry, name, supplier) -> Registry.registerForHolder(registry, name, supplier.get()));
    }

    @Override
    public void sendToServer(Object packet) {

    }

    @Override
    public void sendToPlayer(ServerPlayer player, Object packet) {
        if (packet instanceof ClientboundItemEssencesPacket typed)
            ServerPlayNetworking.send(player, new FabricClientboundItemEssencesPayload(typed));
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
