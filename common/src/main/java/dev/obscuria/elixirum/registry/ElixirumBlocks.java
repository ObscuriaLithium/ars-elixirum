package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.block.GlassCauldronBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface ElixirumBlocks {
    LazyRegister<Block> SOURCE = LazyRegister.create(BuiltInRegistries.BLOCK, Elixirum.MODID);

    LazyValue<Block, GlassCauldronBlock> GLASS_CAULDRON = simple("glass_cauldron", GlassCauldronBlock::new);

    private static <TValue extends Block> LazyValue<Block, TValue>
    simple(final String name, Supplier<TValue> supplier) {
        return SOURCE.register(name, supplier);
    }
}
