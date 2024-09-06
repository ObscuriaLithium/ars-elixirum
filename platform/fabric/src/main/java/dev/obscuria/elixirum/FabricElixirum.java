package dev.obscuria.elixirum;

import dev.obscuria.elixirum.commands.EssenceCommand;
import dev.obscuria.elixirum.commands.RegenerateCommand;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.essence.ItemEssencePreset;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.ServerAlchemy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public class FabricElixirum implements ModInitializer {

    @Override
    public void onInitialize() {
        Elixirum.init();

        DynamicRegistries.registerSynced(ElixirumRegistries.ESSENCE, Essence.DIRECT_CODEC);
        DynamicRegistries.register(ElixirumRegistries.ESSENCE_PRESET, ItemEssencePreset.DIRECT_CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(ServerAlchemy::whenServerStarted);
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(((server, manager) -> ServerAlchemy.whenResourcesReloaded(server)));
        ServerLifecycleEvents.AFTER_SAVE.register(((server, flush, force) -> ServerAlchemy.whenServerSaved(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerAlchemy::whenServerStopped);

        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> {
            EssenceCommand.register(dispatcher, context);
            RegenerateCommand.register(dispatcher, context);
        });
    }
}
