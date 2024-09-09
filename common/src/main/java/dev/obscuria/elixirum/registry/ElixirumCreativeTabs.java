package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.*;
import dev.obscuria.elixirum.common.alchemy.elixir.*;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import static net.minecraft.world.item.CreativeModeTab.Output;

@SuppressWarnings("unused")
public interface ElixirumCreativeTabs {
    LazyRegister<CreativeModeTab> SOURCE = LazyRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Elixirum.MODID);

    LazyValue<CreativeModeTab, CreativeModeTab> ELIXIRUM_GENERIC = SOURCE.register("elixirum_generic",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, -1)
                    .title(createTitle("elixirum_generic"))
                    .icon(() -> ElixirumItems.ELIXIR.value().getDefaultInstance())
                    .displayItems(ElixirumCreativeTabs::generateGeneric)
                    .build());

    LazyValue<CreativeModeTab, CreativeModeTab> ELIXIRUM_EXTRACTS = SOURCE.register("elixirum_extracts",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, -1)
                    .title(createTitle("elixirum_extracts"))
                    .icon(() -> ElixirumItems.EXTRACT.value().getDefaultInstance())
                    .displayItems(ElixirumCreativeTabs::generateExtracts)
                    .build());

    private static Component createTitle(String name) {
        return Component.translatable("itemGroup." + name);
    }

    private static void generateGeneric(ItemDisplayParameters params, Output output) {
        final var essenceGetter = params.holders().lookupOrThrow(ElixirumRegistries.ESSENCE);
        output.accept(ElixirumItems.ELIXIR.value().getDefaultInstance());
        output.accept(testElixir(essenceGetter, 0));
        output.accept(testElixir(essenceGetter, 10));
        output.accept(testElixir(essenceGetter, 20));
        output.accept(testElixir(essenceGetter, 30));
        output.accept(testElixir(essenceGetter, 40));
        output.accept(testElixir(essenceGetter, 50));
        output.accept(testElixir(essenceGetter, 60));
        output.accept(testElixir(essenceGetter, 70));
        output.accept(testElixir(essenceGetter, 80));
        output.accept(testElixir(essenceGetter, 90));
        output.accept(testElixir(essenceGetter, 100));

        output.accept(testByRecipe(params.holders()));
    }

    private static void generateExtracts(ItemDisplayParameters params, Output output) {
        params.holders().lookupOrThrow(ElixirumRegistries.ESSENCE).listElements().forEach(essence -> {
            for (var i = 1; i <= 9; i++) {
                var stack = ElixirumItems.EXTRACT.value().getDefaultInstance();
                stack.set(ElixirumDataComponents.EXTRACT_CONTENTS.value(), new ExtractContents(essence, i));
                output.accept(stack);
            }
        });
    }

    private static ItemStack testElixir(HolderGetter<Essence> getter, double weight) {
        final var stack = ElixirumItems.ELIXIR.value().getDefaultInstance();
        stack.set(ElixirumDataComponents.ELIXIR_STYLE.value(),
                new ElixirStyle(ElixirStyles.Shape.FLASK_2, ElixirStyles.Cap.AMETHYST));

        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS.value(), ElixirContents.create()
                .addEffect(ElixirEffect.byWeight(getter.getOrThrow(ElixirumEssences.ABSORPTION), weight, weight))
                .setCustomColor(DyeColor.MAGENTA.getTextureDiffuseColor())
                .build());
        ElixirContents.setRarityByContent(stack);
        return stack;
    }

    private static ItemStack testByRecipe(HolderLookup.Provider lookup) {
        final var stack = ElixirumItems.ELIXIR.value().getDefaultInstance();
        final var recipe = new ElixirRecipe(List.of(
                Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE,
                Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE,
                Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE,
                Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE, Items.APPLE));
        final var mixer = new ElixirMixer();
        mixer.append(lookup, recipe);
        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS.value(), mixer.brew());
        return stack;
    }
}
