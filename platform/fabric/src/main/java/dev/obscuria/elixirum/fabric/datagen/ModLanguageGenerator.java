package dev.obscuria.elixirum.fabric.datagen;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.section.compendium.ContentsType;
import dev.obscuria.elixirum.common.alchemy.affix.AffixType;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirTier;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceCategory;
import dev.obscuria.elixirum.common.alchemy.style.Cap;
import dev.obscuria.elixirum.common.alchemy.style.Shape;
import dev.obscuria.elixirum.registry.*;
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
        ElixirumBlocks.acceptTranslations(builder::add);
        ElixirumItems.acceptTranslations(builder::add);
        ElixirumMobEffects.acceptTranslations(builder::add);
        ElixirumAttributes.acceptTranslations(builder::add);
        ElixirumCreativeTabs.acceptTranslations(builder::add);
        ElixirumEntityTypes.acceptTranslations(builder::add);

        AffixType.acceptTranslations(builder::add);
        Cap.acceptTranslations(builder::add);
        Shape.acceptTranslations(builder::add);
        ContentsType.acceptTranslations(builder::add);
        EssenceCommand.acceptTranslations(builder::add);
        ElixirTier.acceptTranslations(builder::add);
        EssenceCategory.acceptTranslations(builder::add);

        builder.add("elixirum.functionality_tip", "The functionality of this block has been modified by Ars Elixirum. For more detailed information, click on this icon or the [%s] key during the game.");
        builder.add("elixirum.discovered_essences", "You have discovered %s of %s essences, distributed among %s ingredients.");
        builder.add("elixirum.style.locked", "Discover %s more essences to unlock.");
        builder.add("elixirum.extract.source", "Extracted from %s");
        builder.add("elixirum.extract.essence", "x%s %s");
        builder.add("elixirum.alchemy_properties.title", "Alchemy Properties:");
        builder.add("elixirum.alchemy_properties.essence", " x%s %s");
        builder.add("elixirum.alchemy_properties.affix", " %s");
        builder.add("elixirum.alchemy_properties.unknown", " ???");
        builder.add("elixirum.essence_description.category", " Category: %s");
        builder.add("elixirum.essence_description.max_amplifier", " Max Amplifier: %s");
        builder.add("elixirum.essence_description.max_duration", " Max Duration: %s");
        builder.add("elixirum.essence_description.weak_if", "Weak if quality is less than %s.");
        builder.add("elixirum.essence_description.pale_if", "Pale if less than %s ingredients are used.");

        builder.add("elixir.compound_name", "%s %s of %s");
        builder.add("elixir.collapsed.weak", "+%s Weak Effects");
        builder.add("elixir.collapsed.pale", "+%s Pale Effects");
        builder.add("elixir.status.weak", "Weak");
        builder.add("elixir.status.pale", "Pale");
        builder.add("elixir.status.instantenous", "Instantenous");

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

        DefaultPrefixes.acceptTranslations(builder::add);
    }
}
