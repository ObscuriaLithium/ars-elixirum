package dev.obscuria.elixirum.common;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface ElixirumTags {

    interface Items {
        TagKey<Item> ESSENCE_WHITELIST = TagKey.create(Registries.ITEM, Elixirum.key("essence_whitelist"));
        TagKey<Item> ESSENCE_BLACKLIST = TagKey.create(Registries.ITEM, Elixirum.key("essence_blacklist"));
    }
}
