package dev.obscuria.elixirum;

import dev.obscuria.elixirum.common.alchemy.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.ElixirStyle;
import dev.obscuria.elixirum.network.ClientNetworkHandler;
import dev.obscuria.elixirum.network.FabricClientboundItemEssencesPayload;
import dev.obscuria.elixirum.registry.ElixirumItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.renderer.item.ItemProperties;

public class FabricElixirumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ElixirumClient.init();

        ItemProperties.registerGeneric(Elixirum.key("shape"), ElixirStyle::getShapePredicate);
        ItemProperties.registerGeneric(Elixirum.key("cap"), ElixirStyle::getCapPredicate);

        ColorProviderRegistry.ITEM.register(ElixirContents::getOverlayColor, ElixirumItems.ELIXIR.value());

        PayloadTypeRegistry.playS2C().register(FabricClientboundItemEssencesPayload.TYPE, FabricClientboundItemEssencesPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(FabricClientboundItemEssencesPayload.TYPE,
                (payload, context) -> ClientNetworkHandler.handle(payload.packet(), context.player()));
    }
}
