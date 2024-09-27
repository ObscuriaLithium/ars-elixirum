package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.block.GlassCauldronBlock;
import dev.obscuria.elixirum.common.block.PotionShelfBlock;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public enum ElixirumBlocks {
    GLASS_CAULDRON("glass_cauldron", GlassCauldronBlock::new),
    POTION_SHELF("potion_shelf", PotionShelfBlock::new);

    private final Holder<Block> holder;

    ElixirumBlocks(String name, Supplier<Block> supplier) {
        this.holder = Elixirum.PLATFORM.registerReference(
                BuiltInRegistries.BLOCK, Elixirum.key(name),
                supplier);
    }

    public Holder<Block> holder() {
        return this.holder;
    }

    public Block value() {
        return this.holder.value();
    }

    public static void init() {}
}
