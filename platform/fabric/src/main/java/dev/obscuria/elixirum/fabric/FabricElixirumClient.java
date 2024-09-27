package dev.obscuria.elixirum.fabric;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.ElixirumClient;
import dev.obscuria.elixirum.client.ElixirumKeyMappings;
import dev.obscuria.elixirum.client.ElixirumLayers;
import dev.obscuria.elixirum.client.hooks.ClientHooks;
import dev.obscuria.elixirum.client.model.ModelGlassCauldron;
import dev.obscuria.elixirum.client.particle.ElixirBubbleParticle;
import dev.obscuria.elixirum.client.particle.ElixirSplashParticle;
import dev.obscuria.elixirum.client.renderer.GlassCauldronRenderer;
import dev.obscuria.elixirum.client.renderer.PotionShelfRenderer;
import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.registry.ElixirumBlockEntityTypes;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumParticleTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;

public final class FabricElixirumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ElixirumClient.init();

        WorldRenderEvents.START.register(context -> ClientHooks.onRenderTick());
        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> ClientHooks.onClientTick());
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (ElixirumKeyMappings.MENU.consumeClick())
                ElixirumKeyMappings.menuPressed(minecraft);
        });

        FabricPayload.registerClientReceivers();

        registerItemPredicates();
        registerItemColors();
        registerBlockEntityRenderers();
        registerModelLayers();
        registerParticleProviders();
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
        BlockEntityRenderers.register(ElixirumBlockEntityTypes.GLASS_CAULDRON, GlassCauldronRenderer::new);
        BlockEntityRenderers.register(ElixirumBlockEntityTypes.POTION_SHELF, PotionShelfRenderer::new);
    }

    private static void registerModelLayers() {
        EntityModelLayerRegistry.registerModelLayer(ElixirumLayers.GLASS_CAULDRON, ModelGlassCauldron::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ElixirumLayers.GLASS_CAULDRON_FLUID, ModelGlassCauldron::createFluidLayer);
    }

    private static void registerParticleProviders() {
        ParticleFactoryRegistry.getInstance().register(ElixirumParticleTypes.ELIXIR_BUBBLE, new ElixirBubbleParticle.Provider());
        ParticleFactoryRegistry.getInstance().register(ElixirumParticleTypes.ELIXIR_SPLASH, ElixirSplashParticle.Provider::new);
    }

    private static void registerKeyMappings() {
        KeyBindingHelper.registerKeyBinding(ElixirumKeyMappings.MENU);
    }
}
