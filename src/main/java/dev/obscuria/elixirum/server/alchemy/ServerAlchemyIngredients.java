package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.elixirum.api.alchemy.AlchemyProperties;
import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyIngredients;
import dev.obscuria.elixirum.server.alchemy.generation.IngredientReconciler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;

public final class ServerAlchemyIngredients extends AbstractAlchemyIngredients {

    ServerAlchemyIngredients() {}

    public void load(MinecraftServer server) {
        var packed = AlchemyCodex.INGREDIENTS.load(server);
        this.propertiesMap.clear();
        packed.properties().forEach(this::register);
    }

    public void save(MinecraftServer server) {
        AlchemyCodex.INGREDIENTS.save(server, pack());
    }

    public void reconcile() {
        for (var item : BuiltInRegistries.ITEM.stream().toList()) {
            IngredientReconciler.reconcile(this, item);
        }
    }

    @Override
    public void register(Item item, AlchemyProperties properties) {
        super.register(item, properties);
    }
}