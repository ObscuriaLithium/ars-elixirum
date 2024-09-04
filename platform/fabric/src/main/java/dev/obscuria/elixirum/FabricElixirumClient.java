package dev.obscuria.elixirum;

import dev.obscuria.elixirum.network.ClientNetworkHandler;
import dev.obscuria.elixirum.network.FabricS2CAlchemyMapPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class FabricElixirumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(FabricS2CAlchemyMapPayload.TYPE, FabricS2CAlchemyMapPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(FabricS2CAlchemyMapPayload.TYPE,
                (payload, context) -> ClientNetworkHandler.handle(payload.message(), context.player()));
    }
}
