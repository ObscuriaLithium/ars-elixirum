package dev.obscuria.elixirum.datagen;

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

        builder.add(ElixirumMobEffects.GROW.value(), "Grow");
        builder.add(ElixirumMobEffects.SHRINK.value(), "Shrink");

        builder.add(ElixirumAttributes.POTION_MASTERY.holder(), "Potion Mastery");
        builder.add(ElixirumAttributes.POTION_IMMUNITY.holder(), "Potion Immunity");

        builder.add("commands.elixirum.essence.success.set", "Applied essence %s (x%s) to %s");
        builder.add("commands.elixirum.essence.success.remove", "Removed essence %s from %s");
        builder.add("commands.elixirum.essence.success.clear", "Removed all essences from %s");
        builder.add("commands.elixirum.essence.failed.no_essence", "%s has no essence %s");
        builder.add("commands.elixirum.essence.failed.already_exists", "%s already has essence %s (x%s)");
        builder.add("commands.elixirum.essence.failed.already_empty", "%s has no essences");
    }
}
