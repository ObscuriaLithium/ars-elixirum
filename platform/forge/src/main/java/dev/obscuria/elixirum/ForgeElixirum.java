package dev.obscuria.elixirum;

import net.minecraftforge.fml.common.Mod;

@Mod(Elixirum.MODID)
public class ForgeElixirum {

    public ForgeElixirum() {
        Elixirum.initRegistries();
        Elixirum.init();
    }
}