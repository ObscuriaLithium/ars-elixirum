package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.item.ElixirItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface ElixirumItems {
    LazyRegister<Item> SOURCE = LazyRegister.create(BuiltInRegistries.ITEM, Elixirum.MODID);

    LazyValue<Item, ElixirItem> ELIXIR = simple("elixir", ElixirItem::new);

    private static <TValue extends Item> LazyValue<Item, TValue>
    simple(final String name, Supplier<TValue> supplier) {
        return SOURCE.register(name, supplier);
    }
}
