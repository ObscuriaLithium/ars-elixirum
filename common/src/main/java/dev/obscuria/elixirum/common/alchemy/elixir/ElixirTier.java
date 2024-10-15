package dev.obscuria.elixirum.common.alchemy.elixir;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.function.BiConsumer;

public enum ElixirTier
{
    PALE(Rarity.COMMON, false),
    CLOUDY(Rarity.COMMON, false),
    WEAK(Rarity.COMMON, false),
    MINOR(Rarity.COMMON, false),
    MODERATE(Rarity.COMMON, false),
    GRAND(Rarity.COMMON, false),
    INTENSE(Rarity.UNCOMMON, false),
    SUPREME(Rarity.RARE, false),
    LEGENDARY(Rarity.EPIC, true);

    private final Rarity rarity;
    private final boolean foil;

    ElixirTier(Rarity rarity, boolean foil)
    {
        this.rarity = rarity;
        this.foil = foil;
    }

    public Rarity getRarity()
    {
        return this.rarity;
    }

    public boolean isFoil()
    {
        return this.foil;
    }

    public Component getDisplayName()
    {
        return Component.translatable(getDescriptionId());
    }

    public String getDescriptionId()
    {
        return "elixir.tier." + toString().toLowerCase();
    }

    public static ElixirTier get(ItemStack stack)
    {
        return get(ElixirContents.get(stack));
    }

    public static ElixirTier get(ElixirContents contents)
    {
        return values()[Math.clamp(contents.getQuality() / 10 - 1, 0, 8)];
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer)
    {
        consumer.accept(PALE.getDescriptionId(), "Pale");
        consumer.accept(CLOUDY.getDescriptionId(), "Cloudy");
        consumer.accept(WEAK.getDescriptionId(), "Weak");
        consumer.accept(MINOR.getDescriptionId(), "Minor");
        consumer.accept(MODERATE.getDescriptionId(), "Moderate");
        consumer.accept(GRAND.getDescriptionId(), "Grand");
        consumer.accept(INTENSE.getDescriptionId(), "Intense");
        consumer.accept(SUPREME.getDescriptionId(), "Supreme");
        consumer.accept(LEGENDARY.getDescriptionId(), "Legendary");
    }
}
