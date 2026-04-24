package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.world.item.*;
import dev.obscuria.fragmentum.registry.DeferredItem;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

public interface ElixirumItems {

    DeferredItem<AlchemistEyeItem> ALCHEMIST_EYE = register("alchemist_eye", () -> new AlchemistEyeItem(new Item.Properties().rarity(Rarity.UNCOMMON).durability(512)));
    DeferredItem<GlassCauldronItem> GLASS_CAULDRON = register("glass_cauldron", () -> new GlassCauldronItem(ElixirumBlocks.GLASS_CAULDRON.get(), new Item.Properties().stacksTo(1)));
    DeferredItem<BlockItem> POTION_SHELF = register("potion_shelf", () -> new BlockItem(ElixirumBlocks.POTION_SHELF.get(), new Item.Properties()));
    DeferredItem<ElixirItem> ELIXIR = register("elixir", () -> new ElixirItem(new Item.Properties().stacksTo(8).craftRemainder(Items.GLASS_BOTTLE)));
    DeferredItem<HoneySolventItem> HONEY_SOLVENT = register("honey_solvent", () -> new HoneySolventItem(new Item.Properties()));
    DeferredItem<ExtractItem> EXTRACT = register("extract", () -> new ExtractItem(new Item.Properties()));
    DeferredItem<WitchTotemOfUndyingItem> WITCH_TOTEM_OF_UNDYING = register("witch_totem_of_undying", () -> new WitchTotemOfUndyingItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    DeferredItem<RecordItem> MUSIC_DISC_WUNSCHPUNSCH = register("music_disc_wunschpunsch", () -> new RecordItem(6, ElixirumSounds.RECORD_WUNSCHPUNSCH, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 60));

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
        return ElixirumRegistries.REGISTRAR.registerItem(ArsElixirum.identifier(name), supplier);
    }

    static void init() {}
}
