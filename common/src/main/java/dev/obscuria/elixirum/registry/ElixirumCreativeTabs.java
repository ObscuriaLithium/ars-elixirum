package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.PackedEffect;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.style.Shape;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import static net.minecraft.world.item.CreativeModeTab.Output;

public enum ElixirumCreativeTabs
{
    ELIXIRUM_GENERIC("elixirum_generic",
            () -> ElixirumItems.ALCHEMIST_EYE.value().getDefaultInstance(),
            ElixirumCreativeTabs::tabGeneric),
    ELIXIRUM_EXTRACTS("elixirum_extracts",
            () -> ElixirumItems.EXTRACT.value().getDefaultInstance(),
            ElixirumCreativeTabs::tabExtracts);

    ElixirumCreativeTabs(String name,
                         Supplier<ItemStack> iconSupplier,
                         CreativeModeTab.DisplayItemsGenerator generator)
    {

        ObscureRegistry.register(
                Elixirum.MODID,
                BuiltInRegistries.CREATIVE_MODE_TAB,
                Elixirum.key(name),
                () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, -1)
                        .title(Component.translatable("itemGroup." + name))
                        .icon(iconSupplier)
                        .displayItems(generator)
                        .build());
    }

    private static void tabGeneric(ItemDisplayParameters params, Output output)
    {
        output.accept(ElixirumItems.ALCHEMIST_EYE.value());
        output.accept(ElixirumItems.GLASS_CAULDRON.value());
        output.accept(ElixirumItems.POTION_SHELF.value());
        acceptAllElixirs(params.holders().lookupOrThrow(ElixirumRegistries.ESSENCE), output);
    }

    private static void tabExtracts(ItemDisplayParameters params, Output output)
    {
        params.holders().lookupOrThrow(ElixirumRegistries.ESSENCE).listElements().forEach(essence -> {
            for (var i = 1; i <= 9; i++)
            {
                var stack = ElixirumItems.EXTRACT.value().getDefaultInstance();
                stack.set(ElixirumDataComponents.EXTRACT_CONTENTS, new ExtractContents(Optional.empty(), essence, i));
                output.accept(stack);
            }
        });
    }

    private static void acceptAllElixirs(HolderLookup<Essence> lookup, Output output)
    {
        lookup.listElements().forEach(essence -> acceptVariants(
                ElixirumItems.ELIXIR.value(), ElixirStyle.DEFAULT.withShape(Shape.FLASK_2), essence, output));
        lookup.listElements().forEach(essence -> acceptVariants(
                ElixirumItems.SPLASH_ELIXIR.value(), ElixirStyle.DEFAULT, essence, output));
        lookup.listElements().forEach(essence -> acceptVariants(
                ElixirumItems.WITCH_TOTEM_OF_UNDYING.value(), ElixirStyle.DEFAULT, essence, output));
    }

    private static void acceptVariants(Item item, ElixirStyle style, Holder<Essence> essence, Output output)
    {
        final var stack = item.getDefaultInstance();
        if (style != ElixirStyle.DEFAULT)
            stack.set(ElixirumDataComponents.ELIXIR_STYLE, style);
        output.accept(buildContents(essence, 20, 20).set(stack.copy()));
        output.accept(buildContents(essence, 60, 60).set(stack.copy()));
        output.accept(buildContents(essence, 100, 100).set(stack.copy()));
    }

    private static ElixirContents buildContents(Holder<Essence> essence, double amplifierWeight, double durationWeight)
    {
        return ElixirContents.create()
                .addEffect(PackedEffect.byWeight(essence, amplifierWeight, durationWeight))
                .computeContentColor()
                .build();
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer)
    {
        consumer.accept("itemGroup.elixirum_generic", Elixirum.DISPLAY_NAME);
        consumer.accept("itemGroup.elixirum_extracts", Elixirum.DISPLAY_NAME + ": Extracts");
    }

    public static void init() {}
}
