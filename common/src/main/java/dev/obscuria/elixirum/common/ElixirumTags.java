package dev.obscuria.elixirum.common;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface ElixirumTags
{
    interface Items
    {
        TagKey<Item> ESSENCE_WHITELIST = TagKey.create(Registries.ITEM, Elixirum.key("essence_whitelist"));
        TagKey<Item> ESSENCE_BLACKLIST = TagKey.create(Registries.ITEM, Elixirum.key("essence_blacklist"));
        TagKey<Item> POTION_SHELF_PLACEABLE = TagKey.create(Registries.ITEM, Elixirum.key("potion_shelf_placeable"));
    }

    interface Blocks
    {
        TagKey<Block> HEAT_SOURCES = TagKey.create(Registries.BLOCK, Elixirum.key("heat_sources"));
    }
}
