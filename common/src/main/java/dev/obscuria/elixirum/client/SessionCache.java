package dev.obscuria.elixirum.client;

import com.google.common.collect.Maps;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SessionCache {
    private final Map<ElixirRecipe, ItemStack> stackByRecipe = Maps.newHashMap();
    private final List<ElixirHolder> recentElixirs = Lists.newArrayList();

    @Contract(pure = true)
    public @Unmodifiable List<ElixirHolder> getRecentElixirs() {
        return List.copyOf(recentElixirs);
    }

    public void saveRecent(ElixirRecipe recipe) {
        if (recentElixirs.stream().anyMatch(recent -> recent.is(recipe))) return;
        this.recentElixirs.addFirst(ClientAlchemy.getProfile()
                .searchInCollection(recipe)
                .orElseGet(recipe::asHolder));
    }

    public ItemStack getOrCreateStack(ElixirHolder holder) {
        return stackByRecipe.compute(holder.getRecipe(), (key, value) -> {
            if (value != null) return value;
            return essenceGetter()
                    .map(holder::createStack)
                    .orElseGet(() -> ElixirumItems.ELIXIR.value().getDefaultInstance());
        });
    }

    public void remove(ElixirHolder holder) {
        if (recentElixirs.contains(holder)) return;
        stackByRecipe.remove(holder.getRecipe());
    }

    public void clear() {
        this.stackByRecipe.clear();
        this.recentElixirs.clear();
    }

    private static Optional<HolderGetter<Essence>> essenceGetter() {
        return Optional.ofNullable(Minecraft.getInstance().level)
                .flatMap(level -> level.registryAccess().lookup(ElixirumRegistries.ESSENCE));
    }
}
