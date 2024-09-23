package dev.obscuria.elixirum;

import dev.obscuria.elixirum.client.ElixirumKeyMappings;
import dev.obscuria.elixirum.client.ElixirumLayers;
import dev.obscuria.elixirum.client.model.ModelGlassCauldron;
import dev.obscuria.elixirum.client.particle.ElixirBubbleParticle;
import dev.obscuria.elixirum.client.particle.ElixirSplashParticle;
import dev.obscuria.elixirum.client.renderer.GlassCauldronRenderer;
import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.network.ClientNetworkHandler;
import dev.obscuria.elixirum.network.FabricClientboundDiscoverPayload;
import dev.obscuria.elixirum.network.FabricClientboundItemEssencesPayload;
import dev.obscuria.elixirum.network.FabricClientboundProfilePayload;
import dev.obscuria.elixirum.registry.ElixirumBlockEntityTypes;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumParticleTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;

public class FabricElixirumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ElixirumClient.init();

        WorldRenderEvents.START.register(context -> ElixirumClient.onRenderTick());

        registerItemPredicates();
        registerItemColors();
        registerBlockEntityRenderers();
        registerModelLayers();
        registerParticleProviders();
        registerPayloads();
        registerKeyMappings();
    }

    private static void registerItemPredicates() {
        ItemProperties.registerGeneric(Elixirum.key("shape"), ElixirStyle::getShapePredicate);
        ItemProperties.registerGeneric(Elixirum.key("cap"), ElixirStyle::getCapPredicate);
    }

    private static void registerItemColors() {
        ColorProviderRegistry.ITEM.register(ElixirContents::getOverlayColor, ElixirumItems.ELIXIR.value());
        ColorProviderRegistry.ITEM.register(ExtractContents::getOverlayColor, ElixirumItems.EXTRACT.value());
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ElixirumBlockEntityTypes.GLASS_CAULDRON.value(), GlassCauldronRenderer::new);
    }

    private static void registerModelLayers() {
        EntityModelLayerRegistry.registerModelLayer(ElixirumLayers.GLASS_CAULDRON, ModelGlassCauldron::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ElixirumLayers.GLASS_CAULDRON_FLUID, ModelGlassCauldron::createFluidLayer);
    }

    private static void registerParticleProviders() {
        ParticleFactoryRegistry.getInstance().register(ElixirumParticleTypes.ELIXIR_BUBBLE.value(), new ElixirBubbleParticle.Provider());
        ParticleFactoryRegistry.getInstance().register(ElixirumParticleTypes.ELIXIR_SPLASH.value(), ElixirSplashParticle.Provider::new);
    }

    private static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(
                FabricClientboundItemEssencesPayload.TYPE,
                FabricClientboundItemEssencesPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(FabricClientboundItemEssencesPayload.TYPE,
                (payload, context) -> ClientNetworkHandler.handle(payload.packet(), context.player()));

        PayloadTypeRegistry.playS2C().register(
                FabricClientboundProfilePayload.TYPE,
                FabricClientboundProfilePayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(FabricClientboundProfilePayload.TYPE,
                (payload, context) -> ClientNetworkHandler.handle(payload.packet(), context.player()));

        PayloadTypeRegistry.playS2C().register(
                FabricClientboundDiscoverPayload.TYPE,
                FabricClientboundDiscoverPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(FabricClientboundDiscoverPayload.TYPE,
                (payload, context) -> ClientNetworkHandler.handle(payload.packet(), context.player()));
    }

    private static void registerKeyMappings() {
        KeyBindingHelper.registerKeyBinding(ElixirumKeyMappings.MENU);
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (ElixirumKeyMappings.MENU.consumeClick())
                ElixirumKeyMappings.menuPressed(minecraft);
        });
    }
}
