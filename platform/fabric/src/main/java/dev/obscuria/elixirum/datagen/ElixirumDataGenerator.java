package dev.obscuria.elixirum.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ElixirumDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {

        var pack = generator.createPack();

        pack.addProvider(ModModelGenerator::new);
        pack.addProvider(ModLanguageGenerator::new);

        pack.addProvider(ModEssenceGenerator::new);
        pack.addProvider(ModIngredientPresetGenerator::new);
        pack.addProvider(ModElixirPrefixGenerator::new);
        pack.addProvider(ModTagItemGenerator::new);
        pack.addProvider(ModTagBlockGenerator::new);
    }
}
