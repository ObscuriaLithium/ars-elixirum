package dev.obscuria.elixirum.fabric.datagen;

import dev.obscuria.elixirum.common.ElixirumTags;
import dev.obscuria.elixirum.registry.ElixirumBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

final class ModTagBlockGenerator extends FabricTagProvider.BlockTagProvider {

    public ModTagBlockGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, future);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(ElixirumTags.Blocks.HEAT_SOURCES)
                .addOptionalTag(BlockTags.FIRE.location())
                .addOptionalTag(BlockTags.CAMPFIRES.location())
                .add(reverseLookup(Blocks.MAGMA_BLOCK));

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(reverseLookup(ElixirumBlocks.GLASS_CAULDRON.value()));

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(reverseLookup(ElixirumBlocks.GLASS_CAULDRON.value()));

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(reverseLookup(ElixirumBlocks.POTION_SHELF.value()));
    }
}
