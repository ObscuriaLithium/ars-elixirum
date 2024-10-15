package dev.obscuria.elixirum.fabric;

import dev.obscuria.elixirum.Elixirum;
import net.fabricmc.api.ModInitializer;

public final class FabricElixirum implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        Elixirum.init();
    }
}
