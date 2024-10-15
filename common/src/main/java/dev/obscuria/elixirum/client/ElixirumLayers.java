package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.client.model.geom.ModelLayerLocation;

public interface ElixirumLayers
{
    ModelLayerLocation GLASS_CAULDRON = create("glass_cauldron");
    ModelLayerLocation GLASS_CAULDRON_FLUID = create("glass_cauldron", "fluid");

    private static ModelLayerLocation create(String name)
    {
        return create(name, "main");
    }

    private static ModelLayerLocation create(String name, String layer)
    {
        return new ModelLayerLocation(Elixirum.key(name), layer);
    }
}
