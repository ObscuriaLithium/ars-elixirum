package dev.obscuria.elixirum.common.world.recipe.brewing;

import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.api.alchemy.components.ExtractContents;
import dev.obscuria.elixirum.api.IBrewingRecipe;
import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;

public final class EssenceExtractRecipe implements IBrewingRecipe {

    @Override
    public boolean isReagent(ItemStack reagent) {
        return !Alchemy.guess().ingredients().propertiesOf(reagent).isEmpty();
    }

    @Override
    public boolean matches(ItemStack reagent, ItemStack input1, ItemStack input2, ItemStack input3) {
        if (!isReagent(reagent)) return false;
        return isValidInput(input1) || isValidInput(input2) || isValidInput(input3);
    }

    @Override
    public boolean canPlaceInPotionSlot(ItemStack input) {
        return isValidInput(input);
    }

    @Override
    public ItemStack trueResult(ItemStack reagent, ItemStack input) {
        var result = ElixirumItems.EXTRACT.instantiate();
        var properties = Alchemy.guess().ingredients().propertiesOf(reagent);

        if (ItemStackCache.isBrewFlag(reagent)) {
            var essences = new ArrayList<>(properties.essences().sorted());
            for (var suppressed : ItemStackCache.suppressedEssences(reagent)) {
                essences.removeIf(it -> it.getKey().equals(suppressed));
            }
            if (essences.isEmpty()) return result;

            Collections.shuffle(essences);
            var extracted = essences.get(RandomUtils.nextInt(0, essences.size()));
            var map = EssenceHolderMap.single(extracted.getKey(), extracted.getIntValue());
            ArsElixirumAPI.setExtractContents(result, ExtractContents.create(reagent.getItem(), map));
            ItemStackCache.suppressedEssences(reagent).add(extracted.getKey());
        } else {
            ArsElixirumAPI.setExtractContents(result, ExtractContents.create(reagent.getItem(), properties.essences()));
        }

        return result;
    }

    private boolean isValidInput(ItemStack input) {
        return input.is(ElixirumItems.HONEY_SOLVENT.asItem());
    }
}
