package dev.obscuria.elixirum;

import dev.obscuria.elixirum.common.network.*;
import dev.obscuria.elixirum.server.alchemy.AlchemyCodex;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import dev.obscuria.fragmentum.packs.BuiltInPackBuilder;
import dev.obscuria.fragmentum.server.FragmentumServer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ArsElixirum {

    public static final String MODID = "elixirum";
    public static final String DISPLAY_NAME = "Ars Elixirum";
    public static final Logger LOGGER = LoggerFactory.getLogger(DISPLAY_NAME);

    public static final ResourceLocation FONT = identifier(MODID);
    public static final TagKey<MobEffect> IGNORED_EFFECTS;
    public static final TagKey<Item> IGNORED_ITEMS;
    public static final TagKey<Item> POTION_SHELF_PLACEABLE_ITEMS;
    public static final TagKey<Block> HEAT_SOURCE_BLOCKS;

    public static ResourceLocation identifier(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static void init() {

        FragmentumServer.SERVER_STARTING.connect(ArsElixirum::onServerStart);
        FragmentumServer.SERVER_SAVING.connect(ArsElixirum::onServerSave);
        FragmentumServer.SERVER_STOPPING.connect(ArsElixirum::onServerStop);

        ElixirumRegistries.init();
        AlchemyCodex.init();

        BuiltInPackBuilder.dataPack("packs/classic_alchemy")
                .displayName(Component.literal("Classic Alchemy"))
                .packSource(PackSource.BUILT_IN)
                .register(MODID);

        registerPayloads();
    }

    private static void registerPayloads() {
        var registrar = FragmentumNetworking.registrar(MODID);

        registrar.registerClientbound(
                ClientboundElixirBrewedPayload.class,
                ClientboundElixirBrewedPayload::encode,
                ClientboundElixirBrewedPayload::decode,
                ClientboundElixirBrewedPayload::handle);
        registrar.registerClientbound(
                ClientboundAlchemyPayload.class,
                ClientboundAlchemyPayload::encode,
                ClientboundAlchemyPayload::decode,
                ClientboundAlchemyPayload::handle);
        registrar.registerClientbound(
                ClientboundMasterySyncPayload.class,
                ClientboundMasterySyncPayload::encode,
                ClientboundMasterySyncPayload::decode,
                ClientboundMasterySyncPayload::handle);
        registrar.registerClientbound(
                ClientboundMasteryLevelUpPayload.class,
                ClientboundMasteryLevelUpPayload::encode,
                ClientboundMasteryLevelUpPayload::decode,
                ClientboundMasteryLevelUpPayload::handle);

        registrar.registerServerbound(
                ServerboundRecipeSaveRequest.class,
                ServerboundRecipeSaveRequest::encode,
                ServerboundRecipeSaveRequest::decode,
                ServerboundRecipeSaveRequest::handle);
        registrar.registerServerbound(
                ServerboundSetStyleRequest.class,
                ServerboundSetStyleRequest::encode,
                ServerboundSetStyleRequest::decode,
                ServerboundSetStyleRequest::handle);
        registrar.registerServerbound(
                ServerboundSetChromaRequest.class,
                ServerboundSetChromaRequest::encode,
                ServerboundSetChromaRequest::decode,
                ServerboundSetChromaRequest::handle);
    }

    private static void onServerStart(MinecraftServer server) {
        ServerAlchemy.get(server).onServerStart();
    }

    private static void onServerSave(MinecraftServer server) {
        ServerAlchemy.get(server).onServerSave();
    }

    private static void onServerStop(MinecraftServer server) {
        ServerAlchemy.get(server).onServerStop();
    }

    static {
        IGNORED_EFFECTS = TagKey.create(Registries.MOB_EFFECT, identifier("ignored"));
        HEAT_SOURCE_BLOCKS = TagKey.create(Registries.BLOCK, identifier("heat_source"));
        IGNORED_ITEMS = TagKey.create(Registries.ITEM, identifier("ignored"));
        POTION_SHELF_PLACEABLE_ITEMS = TagKey.create(Registries.ITEM, identifier("potion_shelf_placeable"));
    }
}
