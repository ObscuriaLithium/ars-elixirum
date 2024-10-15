package dev.obscuria.elixirum.registry;

import dev.obscuria.core.api.Deferred;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.block.GlassCauldronBlock;
import dev.obscuria.elixirum.common.block.PotionShelfBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ElixirumBlocks
{
    Deferred<Block, GlassCauldronBlock> GLASS_CAULDRON = register("glass_cauldron", GlassCauldronBlock::new);
    Deferred<Block, PotionShelfBlock> POTION_SHELF = register("potion_shelf", PotionShelfBlock::new);

    private static <T extends Block> Deferred<Block, T> register(final String name,
                                                          Supplier<T> supplier)
    {
        return ObscureRegistry.register(
                Elixirum.MODID,
                BuiltInRegistries.BLOCK,
                Elixirum.key(name),
                supplier);
    }

    static void acceptTranslations(BiConsumer<String, String> consumer)
    {
        consumer.accept(GLASS_CAULDRON.value().getDescriptionId(), "Glass Cauldron");
        consumer.accept(POTION_SHELF.value().getDescriptionId(), "Potion Shelf");
    }

    static void init() {}
}
