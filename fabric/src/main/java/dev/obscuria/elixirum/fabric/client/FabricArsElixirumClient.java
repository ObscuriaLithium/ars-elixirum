package dev.obscuria.elixirum.fabric.client;

import dev.obscuria.elixirum.client.ArsElixirumClient;
import dev.obscuria.elixirum.client.KeyMappings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public final class FabricArsElixirumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ArsElixirumClient.init();
        KeyBindingHelper.registerKeyBinding(KeyMappings.COLLECTION);
    }
}