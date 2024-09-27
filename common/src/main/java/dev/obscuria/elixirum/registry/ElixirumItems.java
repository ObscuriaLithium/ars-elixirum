package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.item.ElixirItem;
import dev.obscuria.elixirum.common.item.ExtractItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public enum ElixirumItems {
    ELIXIR("elixir", ElixirItem::new),
    EXTRACT("extract", ExtractItem::new),
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

    private static Supplier<Item> blockItem(Holder<Block> block, Item.Properties properties) {
        return () -> new BlockItem(block.value(), properties);
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(ElixirumItems.ELIXIR.value().getDescriptionId(), "Elixir");
        consumer.accept(ElixirumItems.EXTRACT.value().getDescriptionId(), "Extract");
    }

    public static void init() {}
}
