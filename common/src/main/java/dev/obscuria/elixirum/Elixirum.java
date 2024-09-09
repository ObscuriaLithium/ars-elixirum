package dev.obscuria.elixirum;

import dev.obscuria.elixirum.platform.IPlatform;
import dev.obscuria.elixirum.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public final class Elixirum {
    public static final String MODID = "elixirum";
    public static final String DISPLAY_NAME = "Ars Elixirum";
    public static final Logger LOG = LoggerFactory.getLogger(DISPLAY_NAME);
    public static final IPlatform PLATFORM = load(IPlatform.class);
    public static final int WATER_COLOR = FastColor.ARGB32.opaque(-13083194);

    public static ResourceLocation key(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public static double getPotionMastery(@Nullable Entity entity) {
        return entity instanceof LivingEntity living
                ? living.getAttributeValue(ElixirumAttributes.POTION_MASTERY.holder())
                : 0.0;
    }

    public static double getPotionImmunity(@Nullable Entity entity) {
        return entity instanceof LivingEntity living
                ? living.getAttributeValue(ElixirumAttributes.POTION_IMMUNITY.holder())
                : 0.0;
    }

    @ApiStatus.Internal
    public static void initRegistries() {
        PLATFORM.register(ElixirumAttributes.SOURCE);
        PLATFORM.register(ElixirumDataComponents.SOURCE);
        PLATFORM.register(ElixirumMobEffects.SOURCE);
        PLATFORM.register(ElixirumItems.SOURCE);
        PLATFORM.register(ElixirumCreativeTabs.SOURCE);
    }

    @ApiStatus.Internal
    public static void init() {}

    @ApiStatus.Internal
    public static <T> T load(Class<T> clazz) {

        final T service = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        LOG.debug("Loaded {} for service {}", service, clazz);
        return service;
    }
}