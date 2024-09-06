package dev.obscuria.elixirum.datagen;


import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ElixirStyles;
import dev.obscuria.elixirum.registry.ElixirumItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModModelsProvider {

    public static void add(boolean run, DataGenerator generator, ExistingFileHelper helper) {
        final var output = generator.getPackOutput();
        //generator.addProvider(run, new ModBlockModels(output, helper));
        generator.addProvider(run, new ItemModels(output, helper));
    }

//    private static class ModBlockModels extends BlockStateProvider {
//        private ModBlockModels(PackOutput output, ExistingFileHelper helper) {
//            super(output, Wunschpunsch.MODID, helper);
//        }
//
//        @Override
//        protected void registerStatesAndModels() {
//            this.getVariantBuilder(WunschpunschBlocks.GLASS_CAULDRON.get())
//                    .partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile(Wunschpunsch.key("block/glass_cauldron"))).addModel();
//            this.models().getBuilder("glass_cauldron").texture("particle", "minecraft:block/light_gray_stained_glass");
//            this.getVariantBuilder(WunschpunschBlocks.POTION_PUDDLE.get())
//                    .partialState().modelForState().modelFile(new ModelFile.UncheckedModelFile(Wunschpunsch.key("block/potion_puddle"))).addModel();
//            this.models().getBuilder("potion_puddle").texture("particle", "wunschpunsch:block/blank");
//            this.horizontalBlock(WunschpunschBlocks.POTION_SHELF.get(), new ModelFile.UncheckedModelFile(Wunschpunsch.key("block/potion_shelf")));
//        }
//    }

    private static class ItemModels extends ItemModelProvider {
        private static final String ELIXIR_DIR = "item/elixir/";

        private ItemModels(PackOutput output, ExistingFileHelper helper) {
            super(output, Elixirum.MODID, helper);
        }

        @Override
        protected void registerModels() {
//            this.basicItem(WunschpunschItems.MUSIC_DISC_WUNSCHPUNSCH.get());
//            this.basicItem(WunschpunschItems.GLASS_CAULDRON.get());
//            this.basicItem(WunschpunschItems.CALCITE_POWDER.get());
//            this.basicItem(WunschpunschItems.SPLASH_BOTTLE.get());
//            this.basicLayeredItem(WunschpunschItems.SPLASH_POTION.get(),
//                    "item/splash_bottle",
//                    "item/splash_bottle_overlay");
//            this.basicLayeredItem(WunschpunschItems.WITCH_TOTEM_OF_UNDYING.get(),
//                    "item/witch_totem_of_undying",
//                    "item/witch_totem_of_undying_overlay");
//            this.getBuilder(WunschpunschItems.POTION_SHELF.get().toString())
//                    .parent(new ModelFile.UncheckedModelFile(Wunschpunsch.key("block/potion_shelf")));
            this.allElixirVariants();
        }

        private ItemModelBuilder generatedModel(Item item, String... layers) {
            final var builder = this.getBuilder(item.toString())
                    .parent(new ModelFile.UncheckedModelFile("item/generated"));
            var index = 0;
            for (var layer : layers)
                builder.texture("layer" + index++, layer);
            return builder;
        }

        private void allElixirVariants() {

            for (var variant : ElixirStyles.allVariants())
                this.getBuilder(ELIXIR_DIR + variant.index())
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", ELIXIR_DIR + variant.shape().getTexture())
                        .texture("layer1", ELIXIR_DIR + variant.shape().getOverlay())
                        .texture("layer2", ELIXIR_DIR + variant.cap().getTexture());

            final var elixir = this.getBuilder(ElixirumItems.ELIXIR.value().toString())
                    .parent(new ModelFile.ExistingModelFile(Elixirum.key(ELIXIR_DIR + 1), existingFileHelper));

            for (var variant : ElixirStyles.allVariants())
                elixir.override()
                        .predicate(Elixirum.key("shape"), variant.shape().getId())
                        .predicate(Elixirum.key("cap"), variant.cap().getId())
                        .model(new ModelFile.ExistingModelFile(Elixirum.key(ELIXIR_DIR + variant.index()), existingFileHelper));
        }
    }
}
