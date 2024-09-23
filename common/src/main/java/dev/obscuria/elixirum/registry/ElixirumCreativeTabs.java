package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.*;
import dev.obscuria.elixirum.common.alchemy.elixir.*;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.item.ElixirItem;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

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
        randomElixirs(params.holders(), output);
    }

    private static void generateExtracts(ItemDisplayParameters params, Output output) {
        params.holders().lookupOrThrow(ElixirumRegistries.ESSENCE).listElements().forEach(essence -> {
            for (var i = 1; i <= 9; i++) {
                var stack = ElixirumItems.EXTRACT.value().getDefaultInstance();
                stack.set(ElixirumDataComponents.EXTRACT_CONTENTS.value(), new ExtractContents(Optional.empty(), essence, i));
                output.accept(stack);
            }
        });
    }

    private static ItemStack testElixir(HolderGetter<Essence> getter, double weight) {
        final var stack = ElixirumItems.ELIXIR.value().getDefaultInstance();
        stack.set(ElixirumDataComponents.ELIXIR_STYLE.value(),
                new ElixirStyle(ElixirShape.FLASK_2, ElixirCap.AMETHYST));

        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS.value(), ElixirContents.create()
                .addEffect(ElixirEffect.byWeight(getter.getOrThrow(ElixirumEssences.ABSORPTION), weight, weight))
                .setCustomColor(DyeColor.MAGENTA.getTextureDiffuseColor())
                .build());
        ElixirContents.setRarityByContent(stack);
        return stack;
    }

    private static void randomElixirs(HolderLookup.Provider lookupProvider, Output output) {
        final var lookup = lookupProvider.lookupOrThrow(ElixirumRegistries.ESSENCE);
        for (var i = 0; i < 90; i++) {
            pickRandom(lookup.listElements().toList()).ifPresent(primaryEssence -> {
                final var weight = 10 + RandomSource.create().nextInt(90);
                final var stack = ElixirumItems.ELIXIR.value().getDefaultInstance();
                final var builder = ElixirContents.create();
                builder.addEffect(ElixirEffect.byWeight(primaryEssence, weight, weight));
                pickRandom(lookup.listElements()
                        .filter(essence -> !essence.is(primaryEssence))
                        .toList())
                        .ifPresent(secondaryEssence -> builder.addEffect(ElixirEffect.byWeight(secondaryEssence, weight / 2.0, weight / 2.0)));
                builder.computeContentColor();
                stack.set(ElixirumDataComponents.ELIXIR_CONTENTS.value(), builder.build());
                ElixirContents.setRarityByContent(stack);
                randomizeName(lookupProvider, stack);
                output.accept(stack);
            });
        }
    }

    private static <V> Optional<V> pickRandom(List<V> collection) {
        if (collection.isEmpty()) return Optional.empty();
        if (collection.size() == 1) return Optional.of(collection.getFirst());
        return Optional.of(collection.get(RandomSource.create().nextInt(collection.size())));
    }

    private static void randomizeName(HolderLookup.Provider lookupProvider, ItemStack stack) {
        final var lookup = lookupProvider.lookupOrThrow(ElixirumRegistries.ELIXIR_PREFIX);
        ElixirContents.getOptional(stack)
                .ifPresent(content -> pickRandom(lookup.listElements()
                        .filter(prefix -> prefix.value().source()
                                .map(source -> source.value().equals(content.effects().get(1).getEssence().getEffect()))
                                .orElse(false))
                        .toList()).ifPresentOrElse(
                        prefix -> stack.set(DataComponents.ITEM_NAME, Component.literal(ElixirItem.getContentQuality(content)
                                + " Elixir '"
                                + Component.translatable(prefix.value().key()).getString()
                                + " "
                                + ElixirItem.getContentName(content)
                                + "'")),
                        () -> stack.set(DataComponents.ITEM_NAME, Component.literal(ElixirItem.getContentQuality(content)
                                + " Elixir '"
                                + ElixirItem.getContentName(content)
                                + "'"))));
    }
}
