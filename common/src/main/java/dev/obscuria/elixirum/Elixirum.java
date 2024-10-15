package dev.obscuria.elixirum;

import dev.obscuria.core.api.ObscureAPI;
import dev.obscuria.core.api.v1.common.ObscureNetworking;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.core.api.v1.server.ObscureServerEvents;
import dev.obscuria.core.api.v1.server.ObscureServerRegistry;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.common.alchemy.elixir.ConfiguredElixir;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirPrefix;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientPreset;
import dev.obscuria.elixirum.common.alchemy.ingredient.Ingredients;
import dev.obscuria.elixirum.network.*;
import dev.obscuria.elixirum.registry.*;
import dev.obscuria.elixirum.server.ServerAlchemy;
import dev.obscuria.elixirum.server.commands.EssenceCommand;
import dev.obscuria.elixirum.server.commands.RegenerateCommand;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Elixirum
{
    public static final String MODID = "elixirum";
    public static final String DISPLAY_NAME = "Ars Elixirum";
    public static final Logger LOG = LoggerFactory.getLogger(DISPLAY_NAME);
    public static final int WATER_COLOR = FastColor.ARGB32.opaque(-13083194);
    public static final Style STYLE = Style.EMPTY.withFont(Elixirum.key("elixirum"));

    public static ResourceLocation key(String name)
    {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public static double getPotionMastery(@Nullable Entity entity)
    {
        return entity instanceof LivingEntity living
                ? living.getAttributeValue(ElixirumAttributes.POTION_MASTERY.holder())
                : 0.0;
    }

    public static double getPotionImmunity(@Nullable Entity entity)
    {
        return entity instanceof LivingEntity living
                ? living.getAttributeValue(ElixirumAttributes.POTION_IMMUNITY.holder())
                : 0.0;
    }

    public static Ingredients getIngredients()
    {
        return ObscureAPI.PLATFORM.isClient()
                ? ClientAlchemy.getIngredients()
                : ServerAlchemy.getIngredients();
    }

    @ApiStatus.Internal
    public static void init()
    {
        ElixirumSounds.init();
        ElixirumAttributes.init();
        ElixirumMobEffects.init();
        ElixirumItems.init();
        ElixirumBlocks.init();
        ElixirumEntityTypes.init();
        ElixirumBlockEntityTypes.init();
        ElixirumDataComponents.init();
        ElixirumParticleTypes.init();
        ElixirumRecipeSerializers.init();
        ElixirumCreativeTabs.init();

        registerEvents();

        ObscureServerRegistry.registerCommand(EssenceCommand::register);
        ObscureServerRegistry.registerCommand(RegenerateCommand::register);

        ObscureRegistry.newSyncedDataRegistry(MODID, ElixirumRegistries.ESSENCE, Essence.DIRECT_CODEC);
        ObscureRegistry.newSyncedDataRegistry(MODID, ElixirumRegistries.ELIXIR_PREFIX, ElixirPrefix.DIRECT_CODEC);
        ObscureRegistry.newSyncedDataRegistry(MODID, ElixirumRegistries.CONFIGURED_ELIXIR, ConfiguredElixir.DIRECT_CODEC);
        ObscureRegistry.newDataRegistry(MODID, ElixirumRegistries.INGREDIENT_PRESET, IngredientPreset.DIRECT_CODEC);

        ObscureNetworking.registerClientbound(MODID,
                ClientboundDiscoverPayload.class,
                ClientboundDiscoverPayload.TYPE,
                ClientboundDiscoverPayload.STREAM_CODEC,
                ClientboundDiscoverPayload::handle);
        ObscureNetworking.registerClientbound(MODID,
                ClientboundIngredientsPayload.class,
                ClientboundIngredientsPayload.TYPE,
                ClientboundIngredientsPayload.STREAM_CODEC,
                ClientboundIngredientsPayload::handle);
        ObscureNetworking.registerClientbound(MODID,
                ClientboundProfilePayload.class,
                ClientboundProfilePayload.TYPE,
                ClientboundProfilePayload.STREAM_CODEC,
                ClientboundProfilePayload::handle);

        ObscureNetworking.registerServerbound(MODID,
                ServerboundCollectionActionPayload.class,
                ServerboundCollectionActionPayload.TYPE,
                ServerboundCollectionActionPayload.STREAM_CODEC,
                ServerboundCollectionActionPayload::handle);
        ObscureNetworking.registerServerbound(MODID,
                ServerboundProfilePayload.class,
                ServerboundProfilePayload.TYPE,
                ServerboundProfilePayload.STREAM_CODEC,
                ServerboundProfilePayload::handle);
    }

    private static void registerEvents()
    {
        ObscureServerEvents.SERVER_STARTED.register(ServerAlchemy::whenServerStarted);
        ObscureServerEvents.START_DATA_PACK_RELOAD.register(((server, manager) -> ServerAlchemy.whenResourcesReloaded(server)));
        ObscureServerEvents.AFTER_SAVE.register(((server, flush, force) -> ServerAlchemy.whenServerSaved(server)));
        ObscureServerEvents.SERVER_STOPPING.register(ServerAlchemy::whenServerStopped);
    }
}