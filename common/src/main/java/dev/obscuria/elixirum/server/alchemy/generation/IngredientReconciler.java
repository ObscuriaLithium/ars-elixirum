package dev.obscuria.elixirum.server.alchemy.generation;

import com.google.common.math.IntMath;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.alchemy.AlchemyIngredientsData;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedIngredient;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;

public enum IngredientReconciler {
    SKIP_IF_EXISTS(IngredientReconciler::skipIfExists),
    SKIP_IF_IGNORED(IngredientReconciler::skipIfIgnored),
    GENERATE_PREDEFINED(IngredientReconciler::generatePredefined),
    GENERATE_RANDOM(IngredientReconciler::generateRandom);

    private final Action action;

    IngredientReconciler(Action action) {
        this.action = action;
    }

    public static void reconcile(AlchemyIngredientsData ingredients, Item item) {
        for (var chain : values()) {
            if (chain.action.reconcile(ingredients, item)) return;
        }
    }

    private static boolean skipIfExists(AlchemyIngredientsData ingredients, Item item) {
        return ingredients.contains(item);
    }

    private static boolean skipIfIgnored(AlchemyIngredientsData ingredients, Item item) {
        return item.builtInRegistryHolder().is(ArsElixirum.IGNORED_ITEMS)
                || item instanceof TieredItem
                || item instanceof BlockItem
                || item == Items.AIR;
    }

    private static boolean generatePredefined(AlchemyIngredientsData ingredients, Item item) {
        return PredefinedIngredient.findFor(item, FragmentumProxy.registryAccess()).map(it -> {
            ingredients.put(item, it.resolve(item, randomFor(item)));
            return true;
        }).orElse(false);
    }

    private static boolean generateRandom(AlchemyIngredientsData ingredients, Item item) {
        ingredients.put(item, PredefinedIngredient.EMPTY.resolve(item, randomFor(item)));
        return true;
    }

    private static RandomSource randomFor(Item item) {
        final var offset = IntMath.pow(BuiltInRegistries.ITEM.getId(item), 3);
        final var seed = FragmentumProxy.server().overworld().getSeed() + offset;
        return RandomSource.create(seed + offset);
    }

    @FunctionalInterface
    public interface Action {

        boolean reconcile(AlchemyIngredientsData ingredients, Item item);
    }
}
