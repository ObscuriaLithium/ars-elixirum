package dev.obscuria.elixirum.forge.client;

import dev.obscuria.elixirum.client.ArsElixirumClient;
import dev.obscuria.elixirum.client.KeyMappings;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ForgeArsElixirumClient {

    public static void init() {
        ArsElixirumClient.init();
        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(ForgeArsElixirumClient::registerKeyMappings);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.COLLECTION);
    }
}
