package dev.obscuria.elixirum.fabric;

import dev.obscuria.elixirum.ArsElixirum;
import net.fabricmc.api.ModInitializer;

public final class FabricArsElixirum implements ModInitializer {

    @Override
    public void onInitialize() {
        ArsElixirum.init();
    }
}