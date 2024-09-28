package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.item.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public enum ElixirumItems implements ItemLike, Supplier<Item> {
    ALCHEMIST_EYE("alchemist_eye", AlchemistEyeItem::new),
    ELIXIR("elixir", ElixirItem::new),
    SPLASH_ELIXIR("splash_elixir", SplashElixirItem::new),
    EXTRACT("extract", ExtractItem::new),
    WITCH_TOTEM_OF_UNDYING("witch_totem_of_undying", WitchTotemOfUndyingItem::new),
    GLASS_CAULDRON("glass_cauldron", blockItem(ElixirumBlocks.GLASS_CAULDRON.holder(), new Item.Properties())),
    POTION_SHELF("potion_shelf", blockItem(ElixirumBlocks.POTION_SHELF.holder(), new Item.Properties()));

    private final Holder<Item> holder;

    ElixirumItems(String name, Supplier<Item> supplier) {
        this.holder = Elixirum.PLATFORM.registerReference(
                BuiltInRegistries.ITEM, Elixirum.key(name),
                supplier);
    }

    public Holder<Item> holder() {
        return this.holder;
    }

    public Item value() {
        return this.holder.value();
    }

    @Override
    public Item asItem() {
        return this.value();
    }

    @Override
    public Item get() {
        return this.value();
    }

    private static Supplier<Item> blockItem(Holder<Block> block, Item.Properties properties) {
        return () -> new BlockItem(block.value(), properties);
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(ALCHEMIST_EYE.value().getDescriptionId(), "Alchemist Eye");
        consumer.accept(ELIXIR.value().getDescriptionId(), "Elixir");
        consumer.accept(SPLASH_ELIXIR.value().getDescriptionId(), "Splash Elixir");
        consumer.accept(EXTRACT.value().getDescriptionId(), "Extract");
        consumer.accept(WITCH_TOTEM_OF_UNDYING.value().getDescriptionId(), "Witch Totem of Undying");
    }

    public static void init() {}
}
