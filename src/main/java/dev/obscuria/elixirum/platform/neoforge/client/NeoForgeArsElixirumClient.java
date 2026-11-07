package dev.obscuria.elixirum.platform.neoforge.client;

import dev.obscuria.elixirum.client.ArsElixirumClient;
import dev.obscuria.elixirum.client.KeyMappings;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class NeoForgeArsElixirumClient {

    public static void init(IEventBus modBus) {
        ArsElixirumClient.init();
        modBus.addListener(NeoForgeArsElixirumClient::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(NeoForgeArsElixirumClient::hideGui);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.COLLECTION);
    }

    private static void hideGui(RenderGuiEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof AlchemyScreen) event.setCanceled(true);
    }
}
