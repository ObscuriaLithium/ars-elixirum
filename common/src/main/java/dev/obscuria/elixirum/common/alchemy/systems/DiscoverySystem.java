package dev.obscuria.elixirum.common.alchemy.systems;

import dev.obscuria.elixirum.api.codex.Alchemy;
import dev.obscuria.elixirum.api.codex.AlchemyProfile;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.codex.components.KnownIngredients;
import net.minecraft.world.item.Item;

public final class DiscoverySystem {

    public static boolean isEssenceKnown(AlchemyProfile profile, Item item, EssenceHolder essence) {
        return profile.knownIngredients().entries.containsKey(item)
                && profile.knownIngredients().entries.get(item).knownEssences.contains(essence);
    }

    public static boolean isAnyEssenceKnown(Alchemy alchemy, AlchemyProfile profile, Item item) {
        for (var entry : alchemy.ingredients().propertiesOf(item).essences().sorted()) {
            if (!isEssenceKnown(profile, item, entry.getKey())) continue;
            return true;
        }
        return false;
    }

    public static boolean isIngredientKnownAsBase(AlchemyProfile profile, Item item) {
        return profile.knownIngredients().entries.containsKey(item)
                && profile.knownIngredients().entries.get(item).knownAsBase;
    }

    public static boolean isIngredientKnownAsCatalyst(AlchemyProfile profile, Item item) {
        return profile.knownIngredients().entries.containsKey(item)
                && profile.knownIngredients().entries.get(item).knownAsCatalyst;
    }

    public static boolean isIngredientKnownAsInhibitor(AlchemyProfile profile, Item item) {
        return profile.knownIngredients().entries.containsKey(item)
                && profile.knownIngredients().entries.get(item).knownAsInhibitor;
    }

    public static boolean discoverEssence(AlchemyProfile profile, Item item, EssenceHolder essence) {
        var entry = profile.knownIngredients().entries.computeIfAbsent(item, DiscoverySystem::newKnownIngredientsEntry);
        return entry.knownEssences.add(essence);
    }

    private static KnownIngredients.Entry newKnownIngredientsEntry(Item item) {
        return KnownIngredients.Entry.empty();
    }
}
