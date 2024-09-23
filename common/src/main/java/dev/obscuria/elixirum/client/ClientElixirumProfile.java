package dev.obscuria.elixirum.client;

import com.google.common.collect.Sets;
import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.network.ClientboundDiscoverPacket;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class ClientElixirumProfile extends ElixirumProfile {

    public boolean isEmpty() {
        return discoveredEssences.isEmpty();
    }

    public boolean isDiscovered(Item item, Holder<Essence> essence) {
        final var essences = discoveredEssences.get(item);
        return essences != null && essences.contains(essence);
    }

    @Contract(pure = true)
    public @Unmodifiable List<ElixirHolder> getSavedPages() {
        return List.copyOf(savedPages);
    }

    public boolean savePage(ElixirHolder page) {
        return savedPages.add(page);
    }

    public boolean isSaved(ElixirRecipe recipe) {
        return savedPages.stream().anyMatch(page -> page.recipe().equals(recipe));
    }

    public boolean removePage(ElixirHolder page) {
        return savedPages.remove(page);
    }

    void handle(ClientboundDiscoverPacket packet) {
        discoveredEssences.compute(packet.item(), (key, value) -> Util.make(
                value == null ? Sets.newHashSet() : value,
                set -> set.add(packet.essence())));
        this.computeTotalDiscoveredEssences();
    }
}
