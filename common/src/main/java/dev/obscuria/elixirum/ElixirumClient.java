package dev.obscuria.elixirum;

import dev.obscuria.core.api.v1.client.ObscureClientRegistry;
import dev.obscuria.elixirum.client.ElixirumKeyMappings;
import dev.obscuria.elixirum.client.ElixirumLayers;
import dev.obscuria.elixirum.client.hooks.ClientHooks;
import dev.obscuria.elixirum.client.model.ModelGlassCauldron;
import dev.obscuria.elixirum.client.particle.ElixirBubbleParticle;
import dev.obscuria.elixirum.client.particle.ElixirSplashParticle;
import dev.obscuria.elixirum.client.renderer.GlassCauldronRenderer;
import dev.obscuria.elixirum.client.renderer.PotionShelfRenderer;
import dev.obscuria.elixirum.client.renderer.ThrownElixirRenderer;
import dev.obscuria.elixirum.client.sound.CauldronSoundInstance;
import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import dev.obscuria.elixirum.registry.ElixirumBlockEntityTypes;
import dev.obscuria.elixirum.registry.ElixirumEntityTypes;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumParticleTypes;
import org.jetbrains.annotations.ApiStatus;

public final class ElixirumClient
{
    public static float getSeconds()
    {
        return ClientHooks.seconds;
    }

    public static void playBoilingSound(GlassCauldronEntity entity)
    {
        CauldronSoundInstance.play(entity);
    }

    @ApiStatus.Internal
    public static void init()
    {
        ObscureClientRegistry.registerItemProperty(Elixirum.MODID, Elixirum.key("shape"), ElixirStyle::getShapePredicate);
        ObscureClientRegistry.registerItemProperty(Elixirum.MODID, Elixirum.key("cap"), ElixirStyle::getCapPredicate);

        ObscureClientRegistry.registerItemColor(Elixirum.MODID, ElixirContents::getOverlayColor, ElixirumItems.ELIXIR);
        ObscureClientRegistry.registerItemColor(Elixirum.MODID, ElixirContents::getOverlayColor, ElixirumItems.SPLASH_ELIXIR);
        ObscureClientRegistry.registerItemColor(Elixirum.MODID, ElixirContents::getOverlayColor, ElixirumItems.WITCH_TOTEM_OF_UNDYING);
        ObscureClientRegistry.registerItemColor(Elixirum.MODID, ExtractContents::getOverlayColor, ElixirumItems.EXTRACT);

        ObscureClientRegistry.registerEntityRenderer(Elixirum.MODID, ElixirumEntityTypes.THROWN_ELIXIR, ThrownElixirRenderer::new);
        ObscureClientRegistry.registerBlockEntityRenderer(Elixirum.MODID, ElixirumBlockEntityTypes.GLASS_CAULDRON, GlassCauldronRenderer::new);
        ObscureClientRegistry.registerBlockEntityRenderer(Elixirum.MODID, ElixirumBlockEntityTypes.POTION_SHELF, PotionShelfRenderer::new);

        ObscureClientRegistry.registerModelLayer(Elixirum.MODID, ElixirumLayers.GLASS_CAULDRON, ModelGlassCauldron::createBodyLayer);
        ObscureClientRegistry.registerModelLayer(Elixirum.MODID, ElixirumLayers.GLASS_CAULDRON_FLUID, ModelGlassCauldron::createFluidLayer);

        ObscureClientRegistry.registerParticleRenderer(Elixirum.MODID, ElixirumParticleTypes.ELIXIR_BUBBLE, new ElixirBubbleParticle.Provider());
        ObscureClientRegistry.registerTexturedParticleRenderer(Elixirum.MODID, ElixirumParticleTypes.ELIXIR_SPLASH, ElixirSplashParticle.Provider::new);

        ObscureClientRegistry.registerKeyMapping(Elixirum.MODID, ElixirumKeyMappings.MENU);

        ClientHooks.init();
    }
}
