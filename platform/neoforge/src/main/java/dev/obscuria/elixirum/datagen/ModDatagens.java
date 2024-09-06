package dev.obscuria.elixirum.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ModDatagens {

    public static void onGatherData(GatherDataEvent event) {
        final var generator = event.getGenerator();
        final var output = generator.getPackOutput();
        final var provider = event.getLookupProvider();
        final var helper = event.getExistingFileHelper();

//        generator.addProvider(event.includeClient(), new ModParticleProvider(output, helper));
//        generator.addProvider(event.includeClient(), new ModSoundsProvider(output, helper));
//        ModLanguagesProvider.add(event.includeClient(), generator);
        ModModelsProvider.add(event.includeClient(), generator, helper);
//
//        generator.addProvider(event.includeServer(), new ModRecipeProvider(output));
//        ModTagsProvider.add(event.includeServer(), generator, provider, helper);
    }
}
