package dev.obscuria.elixirum.fabric;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirPrefix;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientPreset;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.ServerAlchemy;
import dev.obscuria.elixirum.server.commands.EssenceCommand;
import dev.obscuria.elixirum.server.commands.RegenerateCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public final class FabricElixirum implements ModInitializer {

    @Override
    public void onInitialize() {
        Elixirum.init();

        DynamicRegistries.registerSynced(ElixirumRegistries.ESSENCE, Essence.DIRECT_CODEC);
        DynamicRegistries.registerSynced(ElixirumRegistries.ELIXIR_PREFIX, ElixirPrefix.DIRECT_CODEC);
        DynamicRegistries.register(ElixirumRegistries.INGREDIENT_PRESET, IngredientPreset.DIRECT_CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(ServerAlchemy::whenServerStarted);
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(((server, manager) -> ServerAlchemy.whenResourcesReloaded(server)));
        ServerLifecycleEvents.AFTER_SAVE.register(((server, flush, force) -> ServerAlchemy.whenServerSaved(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerAlchemy::whenServerStopped);
        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> {
            EssenceCommand.register(dispatcher, context);
            RegenerateCommand.register(dispatcher, context);
        });

        FabricPayload.registerTypes();
        FabricPayload.registerServerReceivers();
    }
}
