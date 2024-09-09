package dev.obscuria.elixirum.datagen;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.registry.ElixirumAttributes;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumMobEffects;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

final class LanguageGenerator extends FabricLanguageProvider {

    public LanguageGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
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

        builder.add("itemGroup.elixirum_generic", Elixirum.DISPLAY_NAME);
        builder.add("itemGroup.elixirum_extracts", Elixirum.DISPLAY_NAME + ": Extracts");
        builder.add("commands.elixirum.essence.success.set", "Applied essence %s (x%s) to %s");
        builder.add("commands.elixirum.essence.success.remove", "Removed essence %s from %s");
        builder.add("commands.elixirum.essence.success.clear", "Removed all holders from %s");
        builder.add("commands.elixirum.essence.failed.no_essence", "%s has no essence %s");
        builder.add("commands.elixirum.essence.failed.already_exists", "%s already has essence %s (x%s)");
        builder.add("commands.elixirum.essence.failed.already_empty", "%s has no holders");

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
    }
}
