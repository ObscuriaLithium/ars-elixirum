package dev.obscuria.elixirum.common.alchemy.ingredient;

import dev.obscuria.elixirum.ArsElixirum;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

public enum IngredientQuality {
    TIER_1(0, "alchemy/quality_tier_1", Rarity.COMMON),
    TIER_2(1, "alchemy/quality_tier_2", Rarity.UNCOMMON),
    TIER_3(2, "alchemy/quality_tier_3", Rarity.RARE),
    TIER_4(3, "alchemy/quality_tier_4", Rarity.EPIC),
    TIER_5(4, "alchemy/quality_tier_5", null);

    private static final ItemStack DUMMY = Items.AIR.getDefaultInstance();

    public final int index;
    public final TagKey<Item> tag;
    public final @Nullable Rarity rarity;

    IngredientQuality(int index, String tag, @Nullable Rarity rarity) {
        this.index = index;
        this.tag = TagKey.create(Registries.ITEM, ArsElixirum.identifier(tag));
        this.rarity = rarity;
    }

    @SuppressWarnings("deprecation")
    public static IngredientQuality of(Item item) {
        final var entries = IngredientQuality.values();
        for (var entry : entries) {
            if (!item.builtInRegistryHolder().is(entry.tag)) continue;
            return entry;
        }
        for (var entry : entries) {
            if (entry.rarity == null) continue;
            if (!entry.rarity.equals(item.getRarity(DUMMY))) continue;
            return entry;
        }
        return TIER_1;
    }

    public static int selectCount(Item item, RandomSource random) {
        return random.nextIntBetweenInclusive(1, 3) + of(item).index;
    }

    public static int selectWeight(Item item, RandomSource random) {
        return random.nextIntBetweenInclusive(8 + 2 * of(item).index, 16 + 4 * of(item).index);
    }
}
