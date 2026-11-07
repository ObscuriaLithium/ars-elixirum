package dev.obscuria.elixirum.platform.fabric;

import dev.obscuria.elixirum.ArsElixirum;
import net.fabricmc.api.ModInitializer;

public final class FabricArsElixirum implements ModInitializer {

    @Override
    public void onInitialize() {
        ArsElixirum.init();
        //? if >=1.21 {
        /*net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.register(
                server -> dev.obscuria.elixirum.server.alchemy.ServerAlchemy.get(server).onServerStart());
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(
                server -> dev.obscuria.elixirum.server.alchemy.ServerAlchemy.get(server).onServerStop());
        *///? }
    }
}
