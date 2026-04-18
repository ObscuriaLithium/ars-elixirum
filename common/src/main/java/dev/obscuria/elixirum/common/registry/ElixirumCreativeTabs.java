package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.elixirum.common.alchemy.basics.*;
import dev.obscuria.elixirum.common.alchemy.style.Cap;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.Shape;
import dev.obscuria.elixirum.common.alchemy.style.StyleVariant;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public enum ElixirumCreativeTabs {

    GENERAL("general", Icons::general, Contents::general),
    ELIXIRS("elixirs", Icons::elixirs, Contents::elixirs);
    //SPLASH_ELIXIRS("splash_elixirs", Icons::elixirs, Contents::elixirs),
    //LINGERING_ELIXIRS("lingering_elixirs", Icons::elixirs, Contents::elixirs),
    //ETERNAL_RECIPES("eternal_recipes", Icons::elixirs, Contents::elixirs),
    //FORBIDDEN_RECIPES("forbidden_recipes", Icons::elixirs, Contents::elixirs),
    //CUSTOM("custom", Icons::elixirs, Contents::elixirs);

    ElixirumCreativeTabs(String key, Supplier<ItemStack> icon, DisplayItemsGenerator contents) {
        ElixirumRegistries.REGISTRAR.register(Registries.CREATIVE_MODE_TAB, ArsElixirum.identifier(key), () -> CreativeModeTab
                .builder(Row.TOP, 1)
                .title(Component.translatable("itemGroup.elixirum.%s".formatted(key)))
                .displayItems(contents)
                .icon(icon)
                .build());
    }

    interface Icons {

        static ItemStack elixirs() {
            var stack = ElixirumItems.ELIXIR.get().getDefaultInstance();
            ArsElixirumHelper.setStyle(stack, new StyleVariant(Cap.FORGED, Shape.FLASK_2));
            ArsElixirumHelper.setChroma(stack, Chroma.AURORA);
            return stack;
        }

        static ItemStack general() {
            return ElixirumItems.ALCHEMIST_EYE.get().getDefaultInstance();
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

        static void elixirs(ItemDisplayParameters params, Output output) {
            for (var key : Alchemy.guess().essences().asMapView().keySet()) {
                output.accept(createElixir(key, 50.0, Temper.MAX_AMPLIFIER));
                output.accept(createElixir(key, 75.0, Temper.MAX_AMPLIFIER));
                output.accept(createElixir(key, 100.0, Temper.MAX_AMPLIFIER));
                output.accept(createElixir(key, 50.0, Temper.BALANCED));
                output.accept(createElixir(key, 75.0, Temper.BALANCED));
                output.accept(createElixir(key, 100.0, Temper.BALANCED));
                output.accept(createElixir(key, 50.0, Temper.MAX_DURATION));
                output.accept(createElixir(key, 75.0, Temper.MAX_DURATION));
                output.accept(createElixir(key, 100.0, Temper.MAX_DURATION));
            }
        }

        private static ItemStack createElixir(ResourceLocation key, double weight, Temper temper) {
            final var stack = ElixirumItems.ELIXIR.get().getDefaultInstance();
            final var effect = new EffectProvider.Packed(EssenceHolder.getOrCreate(key), weight, temper.value);
            ArsElixirumHelper.setElixirContents(stack, ElixirContents.sorted(List.of(effect)));
            ArsElixirumHelper.setStyle(stack, new StyleVariant(Cap.DEFAULT, Shape.FLASK_2));
            return stack;
        }
    }

    static void init() {}
}
