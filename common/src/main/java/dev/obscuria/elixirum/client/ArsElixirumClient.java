package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.renderer.PotionShelfRenderer;
import dev.obscuria.elixirum.client.renderer.ThrownElixirRenderer;
import dev.obscuria.elixirum.client.screen.tooltip.ClientAlchemyPropertiesTooltip;
import dev.obscuria.elixirum.client.screen.tooltip.ClientElixirContentsTooltip;
import dev.obscuria.elixirum.common.registry.ElixirumEntities;
import dev.obscuria.elixirum.common.registry.ElixirumParticles;
import dev.obscuria.elixirum.common.world.tooltip.AlchemyPropertiesTooltip;
import dev.obscuria.elixirum.common.world.tooltip.EffectsTooltip;
import dev.obscuria.elixirum.client.models.ElixirumModelLayers;
import dev.obscuria.elixirum.client.models.ModelGlassCauldron;
import dev.obscuria.elixirum.client.particles.BubbleParticle;
import dev.obscuria.elixirum.client.particles.SplashParticle;
import dev.obscuria.elixirum.client.renderer.GlassCauldronRenderer;
import dev.obscuria.elixirum.client.screen.tooltip.ClientCompositionTooltip;
import dev.obscuria.elixirum.common.registry.ElixirumBlockEntities;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.tooltip.CompositionTooltip;
import dev.obscuria.elixirum.client.screen.tooltip.ClientEffectsTooltip;
import dev.obscuria.elixirum.common.world.tooltip.ElixirContentsTooltip;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import dev.obscuria.elixirum.helpers.StyleHelper;
import dev.obscuria.fragmentum.client.FragmentumClientRegistry;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class ArsElixirumClient {

    private static final long startTime = System.currentTimeMillis();

    public static float timer() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public static void init() {
        FragmentumClientRegistry.registerTooltipComponent(AlchemyPropertiesTooltip.class, ClientAlchemyPropertiesTooltip::new);
        FragmentumClientRegistry.registerTooltipComponent(ElixirContentsTooltip.class, ClientElixirContentsTooltip::new);
        FragmentumClientRegistry.registerTooltipComponent(CompositionTooltip.class, ClientCompositionTooltip::of);
        FragmentumClientRegistry.registerTooltipComponent(EffectsTooltip.class, ClientEffectsTooltip::of);

        final var registrar = FragmentumClientRegistry.registrar(ArsElixirum.MODID);

        registrar.registerModelLayer(ElixirumModelLayers.GLASS_CAULDRON, ModelGlassCauldron::createMainLayer);
        registrar.registerModelLayer(ElixirumModelLayers.GLASS_CAULDRON_FLUID, ModelGlassCauldron::createFluidLayer);
        registrar.registerEntityRenderer(ElixirumEntities.THROWN_ELIXIR, ThrownElixirRenderer::new);
        registrar.registerBlockEntityRenderer(ElixirumBlockEntities.GLASS_CAULDRON, GlassCauldronRenderer::new);
        registrar.registerBlockEntityRenderer(ElixirumBlockEntities.POTION_SHELF, PotionShelfRenderer::new);

        registrar.registerItemProperty(ArsElixirum.identifier("cap"), ArsElixirumClient::capToProperty);
        registrar.registerItemProperty(ArsElixirum.identifier("shape"), ArsElixirumClient::shapeToProperty);
        registrar.registerItemColor(ArsElixirumClient::pickElixirColor, ElixirumItems.ELIXIR);
        registrar.registerItemColor(ArsElixirumClient::pickExtractColor, ElixirumItems.EXTRACT);
        registrar.registerItemColor(ArsElixirumClient::pickElixirColor, ElixirumItems.WITCH_TOTEM_OF_UNDYING);

        registrar.registerTexturedParticleRenderer(ElixirumParticles.SPLASH, SplashParticle.Provider::new);
        registrar.registerParticleRenderer(ElixirumParticles.BUBBLE, new BubbleParticle.Provider());
    }

    public static float capToProperty(ItemStack stack, @Nullable Level level, @Nullable Entity entity, int seed) {
        return StyleHelper.style(stack).cap().id / 100f;
    }

    public static float shapeToProperty(ItemStack stack, @Nullable Level level, @Nullable Entity entity, int seed) {
        return StyleHelper.style(stack).shape().id / 100f;
    }

    private static int pickElixirColor(ItemStack stack, int layer) {
        return layer != 1 ? -1 : elixirColorOf(stack).decimal();
    }

    private static int pickExtractColor(ItemStack stack, int layer) {
        return layer != 1 ? -1 : extractColorOf(stack).decimal();
    }

    private static RGB elixirColorOf(ItemStack stack) {
        return StyleHelper.chroma(stack).computeColor(ContentsHelper.elixir(stack));
    }

    private static RGB extractColorOf(ItemStack stack) {
        return ContentsHelper.extract(stack).essences().dominantColor();
    }
}
