package dev.obscuria.elixirum.client.models;

import dev.obscuria.elixirum.ArsElixirum;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class ElixirumModelLayers {

    public static final ModelLayerLocation GLASS_CAULDRON = create("glass_cauldron", "main");
    public static final ModelLayerLocation GLASS_CAULDRON_FLUID = create("glass_cauldron", "fluid");

    private static ModelLayerLocation create(String name, String layer) {
        return new ModelLayerLocation(ArsElixirum.identifier(name), layer);
    }
}
