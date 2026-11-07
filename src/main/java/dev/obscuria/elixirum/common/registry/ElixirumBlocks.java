package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.world.block.GlassCauldronBlock;
import dev.obscuria.elixirum.common.world.block.PotionShelfBlock;
import dev.obscuria.fragmentum.registry.DeferredBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public interface ElixirumBlocks {

    DeferredBlock<GlassCauldronBlock> GLASS_CAULDRON = register("glass_cauldron",
            () -> new GlassCauldronBlock(BlockBehaviour.Properties
                    .copy(Blocks.BLACK_STAINED_GLASS)
                    .requiresCorrectToolForDrops()
                    .mapColor(MapColor.STONE)
                    .strength(2.0f)
                    .noOcclusion()));
    DeferredBlock<PotionShelfBlock> POTION_SHELF = register("potion_shelf",
            () -> new PotionShelfBlock(BlockBehaviour.Properties
                    .copy(Blocks.SPRUCE_PLANKS)
                    .noOcclusion()));

    private static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier) {
        return ElixirumRegistries.REGISTRAR.registerBlock(ArsElixirum.identifier(name), supplier);
    }

    static void init() {}
}
