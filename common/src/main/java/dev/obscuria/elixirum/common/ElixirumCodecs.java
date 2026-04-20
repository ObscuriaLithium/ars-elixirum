package dev.obscuria.elixirum.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class ElixirumCodecs {

    public static final Codec<Item> ITEM_NO_FALLBACK;

    private static DataResult<Item> itemByKey(ResourceLocation key) {
        var item = BuiltInRegistries.ITEM.get(key);
        return item != Items.AIR
                ? DataResult.success(item)
                : DataResult.error(() -> "Unknown item '%s'".formatted(key));
    }

    static {
        ITEM_NO_FALLBACK = ResourceLocation.CODEC.comapFlatMap(ElixirumCodecs::itemByKey, BuiltInRegistries.ITEM::getKey);
    }
}
