package dev.obscuria.elixirum.client;

import com.google.common.collect.Maps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.model.ModelGlassCauldron;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.HashMap;
import java.util.function.Supplier;

public interface ElixirumLayers {
    HashMap<ModelLayerLocation, Supplier<LayerDefinition>> VALUES = Maps.newHashMap();

    ModelLayerLocation GLASS_CAULDRON = add("glass_cauldron", ModelGlassCauldron::createBodyLayer);
    ModelLayerLocation GLASS_CAULDRON_FLUID = add("glass_cauldron", "fluid", ModelGlassCauldron::createFluidLayer);

    private static ModelLayerLocation add(String name, Supplier<LayerDefinition> model) {
        return add(name, "main", model);
    }

    private static ModelLayerLocation add(String name, String layer, Supplier<LayerDefinition> model) {
        final var location = new ModelLayerLocation(Elixirum.key(name), layer);
        VALUES.put(location, model);
        return location;
    }
}
