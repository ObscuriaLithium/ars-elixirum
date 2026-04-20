package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.elixirum.common.alchemy.AlchemyIngredientsData;
import dev.obscuria.elixirum.api.AlchemyIngredientsView;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import dev.obscuria.elixirum.server.alchemy.generation.IngredientReconciler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

public final class ServerAlchemyIngredients implements AlchemyIngredientsView {

    private AlchemyIngredientsData data = AlchemyIngredientsData.empty();

    ServerAlchemyIngredients() {}

    void load(MinecraftServer server) {
        data = AlchemyCodex.INGREDIENTS.load(server);
    }

    void save(MinecraftServer server) {
        AlchemyCodex.INGREDIENTS.save(server, data);
    }

    void reconcile() {
        BuiltInRegistries.ITEM.forEach(item -> IngredientReconciler.reconcile(data, item));
    }

    @Nullable CompoundTag pack() {
        return AlchemyIngredientsData.CODEC
                .encodeStart(NbtOps.INSTANCE, data)
                .map(CompoundTag.class::cast)
                .result().orElse(null);
    }

    @Override
    public int totalIngredients() {
        return data.totalIngredients();
    }

    @Override
    public int totalEssences() {
        return data.totalEssences();
    }

    @Override
    public AlchemyProperties propertiesOf(Item item) {
        return data.propertiesOf(item);
    }

    @Override
    public AlchemyProperties propertiesOf(ItemStack stack) {
        return data.propertiesOf(stack);
    }

    @Override
    public Map<Item, AlchemyProperties> asMapView() {
        return data.asMapView();
    }
}
