package dev.obscuria.elixirum.fabric;

import dev.obscuria.elixirum.ElixirumClient;
import net.fabricmc.api.ClientModInitializer;

public final class FabricElixirumClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ElixirumClient.init();
    }
}
