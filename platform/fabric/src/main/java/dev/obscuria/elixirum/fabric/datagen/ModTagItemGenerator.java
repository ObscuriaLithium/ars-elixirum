package dev.obscuria.elixirum.fabric.datagen;

import dev.obscuria.elixirum.common.ElixirumTags;
import dev.obscuria.elixirum.registry.ElixirumItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

final class ModTagItemGenerator extends FabricTagProvider.ItemTagProvider {

    public ModTagItemGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, future);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(ElixirumTags.Items.ESSENCE_WHITELIST)
                .add(reverseLookup(Items.SWEET_BERRIES))
                .add(reverseLookup(Items.SPORE_BLOSSOM))
                .add(reverseLookup(Items.BAMBOO))
                .add(reverseLookup(Items.SUGAR_CANE))
                .add(reverseLookup(Items.CACTUS))
                .add(reverseLookup(Items.CRIMSON_ROOTS))
                .add(reverseLookup(Items.WARPED_ROOTS))
                .add(reverseLookup(Items.NETHER_SPROUTS))
                .add(reverseLookup(Items.WEEPING_VINES))
                .add(reverseLookup(Items.TWISTING_VINES))
                .add(reverseLookup(Items.VINE))
                .add(reverseLookup(Items.BIG_DRIPLEAF))
                .add(reverseLookup(Items.GLOW_LICHEN))
                .add(reverseLookup(Items.HANGING_ROOTS))
                .add(reverseLookup(Items.FROGSPAWN))
                .add(reverseLookup(Items.TURTLE_EGG))
                .add(reverseLookup(Items.SNIFFER_EGG))
                .add(reverseLookup(Items.COCOA_BEANS))
                .add(reverseLookup(Items.GLOW_BERRIES))
                .add(reverseLookup(Items.KELP))
                .add(reverseLookup(Items.SCULK_VEIN))
                .add(reverseLookup(Items.STRING))
                .add(reverseLookup(Items.REDSTONE));

        this.tag(ElixirumTags.Items.ESSENCE_BLACKLIST)
                .add(reverseLookup(Items.BUCKET))
                .add(reverseLookup(Items.WATER_BUCKET))
                .add(reverseLookup(Items.LAVA_BUCKET))
                .add(reverseLookup(Items.MILK_BUCKET))
                .add(reverseLookup(Items.AXOLOTL_BUCKET))
                .add(reverseLookup(Items.COD_BUCKET))
                .add(reverseLookup(Items.POWDER_SNOW_BUCKET))
                .add(reverseLookup(Items.PUFFERFISH_BUCKET))
                .add(reverseLookup(Items.SALMON_BUCKET))
                .add(reverseLookup(Items.TADPOLE_BUCKET))
                .add(reverseLookup(Items.TROPICAL_FISH_BUCKET));

        this.tag(ElixirumTags.Items.POTION_SHELF_PLACEABLE)
                .add(reverseLookup(ElixirumItems.ELIXIR.value()))
                .add(reverseLookup(ElixirumItems.EXTRACT.value()))
                .add(reverseLookup(Items.GLASS_BOTTLE))
                .add(reverseLookup(Items.POTATO))
                .add(reverseLookup(Items.SPLASH_POTION))
                .add(reverseLookup(Items.LINGERING_POTION))
                .add(reverseLookup(Items.EXPERIENCE_BOTTLE))
                .add(reverseLookup(Items.HONEY_BOTTLE))
                .add(reverseLookup(Items.OMINOUS_BOTTLE))
                .add(reverseLookup(Items.DRAGON_BREATH));
    }
}
