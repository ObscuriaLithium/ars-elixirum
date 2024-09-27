package dev.obscuria.elixirum.fabric.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;
import dev.obscuria.elixirum.registry.ElixirumItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.item.Items;

final class ModModelGenerator extends FabricModelProvider {
    private static final String ELIXIR_DIR = "item/elixir/";

    public ModModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generateElixirVariants(generators);
        generateBaseElixir(generators);
        generators.generateLayeredItem(ModelLocationUtils.getModelLocation(ElixirumItems.EXTRACT.value()),
                Elixirum.key("item/extract"),
                Elixirum.key("item/extract_overlay"));
    }

    private void generateElixirVariants(ItemModelGenerators generators) {
        StyleVariant.allVariants()
                .forEach(variant -> generators.generateLayeredItem(Elixirum.key(ELIXIR_DIR + variant.index()),
                        Elixirum.key(ELIXIR_DIR + variant.shape().getTexture()),
                        Elixirum.key(ELIXIR_DIR + variant.shape().getOverlay()),
                        Elixirum.key(ELIXIR_DIR + variant.cap().getTexture())));
    }

    private void generateBaseElixir(ItemModelGenerators generators) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(ElixirumItems.ELIXIR.value()),
                TextureMapping.layer0(Items.GLASS_BOTTLE), generators.output,
                (location, map) -> {
                    var base = ModelTemplates.FLAT_ITEM.createBaseTemplate(location, map);
                    var overrides = new JsonArray();
                    StyleVariant.allVariants()
                            .forEach(variant -> {
                                var override = new JsonObject();
                                override.addProperty("model", Elixirum.key(ELIXIR_DIR + variant.index()).toString());
                                var predicate = new JsonObject();
                                predicate.addProperty(Elixirum.key("shape").toString(), variant.shape().getPredicate());
                                predicate.addProperty(Elixirum.key("cap").toString(), variant.cap().getPredicate());
                                override.add("predicate", predicate);
                                overrides.add(override);
                            });
                    base.add("overrides", overrides);
                    return base;
                });
    }
}
