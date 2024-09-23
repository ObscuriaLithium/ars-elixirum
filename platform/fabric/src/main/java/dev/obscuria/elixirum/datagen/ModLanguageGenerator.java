package dev.obscuria.elixirum.datagen;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.section.compendium.ContentsType;
import dev.obscuria.elixirum.common.alchemy.affix.AffixType;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirCap;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirShape;
import dev.obscuria.elixirum.registry.ElixirumAttributes;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumMobEffects;
import dev.obscuria.elixirum.server.commands.EssenceCommand;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

final class ModLanguageGenerator extends FabricLanguageProvider {

    public ModLanguageGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        builder.add(ElixirumItems.ELIXIR.value(), "Elixir");
        builder.add(ElixirumItems.EXTRACT.value(), "Extract");

        builder.add(ElixirumMobEffects.GROW.value(), "Grow");
        builder.add(ElixirumMobEffects.SHRINK.value(), "Shrink");

        builder.add(ElixirumAttributes.POTION_MASTERY.holder(), "Potion Mastery");
        builder.add(ElixirumAttributes.POTION_IMMUNITY.holder(), "Potion Immunity");

        AffixType.acceptTranslations(builder::add);
        ElixirCap.acceptTranslations(builder::add);
        ElixirShape.acceptTranslations(builder::add);
        ContentsType.acceptTranslations(builder::add);
        EssenceCommand.acceptTranslations(builder::add);

        builder.add("elixirum.discovered_essences", "You have discovered %s of %s essences, distributed among %s ingredients.");
        builder.add("elixirum.style.locked", "Discover %s more essences to unlock.");

        builder.add("itemGroup.elixirum_generic", Elixirum.DISPLAY_NAME);
        builder.add("itemGroup.elixirum_extracts", Elixirum.DISPLAY_NAME + ": Extracts");

        builder.add("elixirum.extract.source", "Extracted from %s");
        builder.add("elixirum.extract.essence", "x%s %s");

        builder.add("elixir.quality.1", "Pale");
        builder.add("elixir.quality.2", "Cloudy");
        builder.add("elixir.quality.3", "Weak");
        builder.add("elixir.quality.4", "Minor");
        builder.add("elixir.quality.5", "Moderate");
        builder.add("elixir.quality.6", "Grand");
        builder.add("elixir.quality.7", "Intense");
        builder.add("elixir.quality.8", "Supreme");
        builder.add("elixir.quality.9", "Legendary");

        builder.add("potion.potency.6", "VI");
        builder.add("potion.potency.7", "VII");
        builder.add("potion.potency.8", "VIII");
        builder.add("potion.potency.9", "IX");
        builder.add("potion.potency.10", "X");
        builder.add("potion.potency.11", "XI");
        builder.add("potion.potency.12", "XII");
        builder.add("potion.potency.13", "XIII");
        builder.add("potion.potency.14", "XIV");
        builder.add("potion.potency.15", "XV");
        builder.add("potion.potency.16", "XVI");
        builder.add("potion.potency.17", "XVII");
        builder.add("potion.potency.18", "XVIII");
        builder.add("potion.potency.19", "XX");

        builder.add("category.elixirum", Elixirum.DISPLAY_NAME);
        builder.add("key.elixirum.menu", "Open Menu");

        builder.add("tab.elixirum.recent", "Recent");
        builder.add("tab.elixirum.collection", "Collection");
        builder.add("tab.elixirum.workshop", "Workshop");
        builder.add("tab.elixirum.recipe_books", "Recipe Books");
        builder.add("tab.elixirum.compendium", "Compendium");

        DefaultPrefixes.acceptTranslations(builder::add);
    }
}
