package dev.obscuria.elixirum;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Elixirum.MODID)
public class NeoForgeElixirum {

    public NeoForgeElixirum(IEventBus eventBus) {
        Elixirum.init();
    }
}