package dev.obscuria.elixirum.registry;

import dev.obscuria.core.api.Deferred;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ElixirumItems
{
    Deferred<Item, AlchemistEyeItem> ALCHEMIST_EYE = register("alchemist_eye", AlchemistEyeItem::new);
    Deferred<Item, ElixirItem> ELIXIR = register("elixir", ElixirItem::new);
    Deferred<Item, SplashElixirItem> SPLASH_ELIXIR = register("splash_elixir", SplashElixirItem::new);
    Deferred<Item, ExtractItem> EXTRACT = register("extract", ExtractItem::new);
    Deferred<Item, WitchTotemOfUndyingItem> WITCH_TOTEM_OF_UNDYING = register("witch_totem_of_undying", WitchTotemOfUndyingItem::new);
    Deferred<Item, BlockItem> GLASS_CAULDRON = register("glass_cauldron", blockItem(ElixirumBlocks.GLASS_CAULDRON, new Item.Properties()));
    Deferred<Item, BlockItem> POTION_SHELF = register("potion_shelf", blockItem(ElixirumBlocks.POTION_SHELF, new Item.Properties()));

    private static <T extends Item> Deferred<Item, T> register(String name,
                                                               Supplier<T> supplier)
    {
        return ObscureRegistry.register(
                Elixirum.MODID,
                BuiltInRegistries.ITEM,
                Elixirum.key(name),
                supplier);
    }

    private static Supplier<BlockItem>
    blockItem(Deferred<Block, ? extends Block> block,
              Item.Properties properties)
    {
        return () -> new BlockItem(block.value(), properties);
    }

    static void acceptTranslations(BiConsumer<String, String> consumer)
    {
        consumer.accept(ALCHEMIST_EYE.value().getDescriptionId(), "Alchemist Eye");
        consumer.accept(ELIXIR.value().getDescriptionId(), "Elixir");
        consumer.accept(SPLASH_ELIXIR.value().getDescriptionId(), "Splash Elixir");
        consumer.accept(EXTRACT.value().getDescriptionId(), "Extract");
        consumer.accept(WITCH_TOTEM_OF_UNDYING.value().getDescriptionId(), "Witch Totem of Undying");
    }

    static void init() {}
}
