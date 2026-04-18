package dev.obscuria.elixirum.forge;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.forge.client.ForgeArsElixirumClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(ArsElixirum.MODID)
public class ForgeArsElixirum {

    public ForgeArsElixirum() {
        ArsElixirum.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ForgeArsElixirumClient::init);
    }
}