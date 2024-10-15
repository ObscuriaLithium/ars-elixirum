package dev.obscuria.elixirum;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(
        value = Elixirum.MODID,
        dist = Dist.CLIENT)
public final class NeoElixirumClient
{
    public NeoElixirumClient(IEventBus eventBus)
    {
        ElixirumClient.init();
    }
}
