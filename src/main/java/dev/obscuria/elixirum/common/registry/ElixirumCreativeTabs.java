package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.api.alchemy.components.StyleVariant;
import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy.providers.PackedEffectProvider;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.common.alchemy.traits.Focus;
import dev.obscuria.elixirum.common.alchemy.traits.Risk;
import dev.obscuria.elixirum.common.alchemy.styles.Cap;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.common.alchemy.styles.Shape;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.helpers.StyleHelper;
import dev.obscuria.fragmentum.registry.DeferredItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public enum ElixirumCreativeTabs {

    GENERAL("general", Icons::general, Contents::general),
    ELIXIRS("elixirs", Icons::elixir,
            (parameters, output) -> Contents.generate(output,
                    ElixirumItems.ELIXIR,
                    Form.POTABLE,
                    StyleVariant.elixirVariant())),
    SPLASH_ELIXIRS("splash_elixirs", Icons::splashElixir,
            (parameters, output) -> Contents.generate(output,
                    ElixirumItems.ELIXIR,
                    Form.EXPLOSIVE,
                    StyleVariant.splashElixirVariant())),
    LINGERING_ELIXIRS("lingering_elixirs", Icons::lingeringElixir,
            (parameters, output) -> Contents.generate(output,
                    ElixirumItems.ELIXIR,
                    Form.LINGERING,
                    StyleVariant.lingeringElixirVariant())),
    TOTEMS("totems", Icons::totem,
            (parameters, output) -> Contents.generate(output,
                    ElixirumItems.WITCH_TOTEM_OF_UNDYING,
                    Form.POTABLE,
                    StyleVariant.defaultVariant()));

    private static final double[] WEIGHTS = {50.0, 75.0, 100.0};
    private static final Focus[] FOCUSES = {Focus.MAX_DURATION, Focus.BALANCED, Focus.MAX_POTENCY};

    ElixirumCreativeTabs(String key, Supplier<ItemStack> icon, DisplayItemsGenerator contents) {
        ElixirumRegistries.REGISTRAR.register(Registries.CREATIVE_MODE_TAB, ArsElixirum.identifier(key), () -> CreativeModeTab
                .builder(Row.TOP, 1)
                .title(Component.translatable("itemGroup.elixirum.%s".formatted(key)))
                .displayItems(contents)
                .icon(icon)
                .build());
    }

    interface Icons {

        static ItemStack general() {
            return ElixirumItems.ALCHEMIST_EYE.get().getDefaultInstance();
        }

        static ItemStack elixir() {
            return styledElixir(StyleVariant.of(Cap.FORGED, Shape.FLASK_2));
        }

        static ItemStack splashElixir() {
            return styledElixir(StyleVariant.of(Cap.LID, Shape.FLASK_2));
        }

        static ItemStack lingeringElixir() {
            return styledElixir(StyleVariant.of(Cap.CROWN, Shape.FLASK_2));
        }

        static ItemStack styledElixir(StyleVariant style) {
            var stack = ElixirumItems.ELIXIR.get().getDefaultInstance();
            StyleHelper.setStyle(stack, style);
            StyleHelper.setChroma(stack, Chroma.AURORA);
            return stack;
        }

        static ItemStack totem() {
            var stack = ElixirumItems.WITCH_TOTEM_OF_UNDYING.get().getDefaultInstance();
            StyleHelper.setChroma(stack, Chroma.AURORA);
            return stack;
        }
    }

    interface Contents {

        static void general(ItemDisplayParameters params, Output output) {
            output.accept(ElixirumItems.ALCHEMIST_EYE);
            output.accept(ElixirumItems.GLASS_CAULDRON);
            output.accept(ElixirumItems.POTION_SHELF);
            output.accept(ElixirumItems.HONEY_SOLVENT);
            output.accept(ElixirumItems.MUSIC_DISC_WUNSCHPUNSCH);
        }

        static void generate(
                Output output,
                DeferredItem<?> item,
                Form application,
                StyleVariant style
        ) {
            Alchemy.guess().essences().forEachHolder(essence -> {
                for (var focus : FOCUSES)
                    for (var weight : WEIGHTS)
                        output.accept(create(item, essence, weight, focus, application, style));
            });
        }

        private static ItemStack create(
                DeferredItem<?> item,
                EssenceHolder essence,
                double weight,
                Focus focus,
                Form form,
                StyleVariant style
        ) {
            var stack = item.instantiate();
            var effect = new PackedEffectProvider(essence, weight, focus.value);
            var contents = ElixirContents.create(List.of(effect), focus, form, Risk.BALANCED);
            ArsElixirumAPI.setElixirContents(stack, contents);
            StyleHelper.setStyle(stack, style);
            return stack;
        }
    }

    static void init() {}
}