package dev.obscuria.elixirum.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ElixirumDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {

        var pack = generator.createPack();

        pack.addProvider(ModelGenerator::new);
        pack.addProvider(LanguageGenerator::new);

        pack.addProvider(EssenceGenerator::new);
        pack.addProvider(EssencePresetGenerator::new);
        pack.addProvider(ItemTagGenerator::new);
    }
}
