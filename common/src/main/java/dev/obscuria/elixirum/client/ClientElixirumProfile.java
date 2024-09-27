package dev.obscuria.elixirum.client;

import com.google.common.collect.Sets;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.ElixirumProfile;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.network.ClientboundDiscoverPacket;
import dev.obscuria.elixirum.network.ClientboundProfilePacket;
import dev.obscuria.elixirum.network.ServerboundProfilePacket;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ClientElixirumProfile extends ElixirumProfile {
    private boolean changed;

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public boolean isDiscovered(Item item, Holder<Essence> essence) {
        final var essences = discoveredEssences.get(item);
        return essences != null && essences.contains(essence);
    }

    @Contract(pure = true)
    public @Unmodifiable List<ElixirHolder> getCollection() {
        return List.copyOf(collection);
    }

    public Optional<ElixirHolder> searchInCollection(ElixirRecipe recipe) {
        for (var holder : getCollection())
            if (holder.is(recipe))
                return Optional.of(holder);
        return Optional.empty();
    }

    public boolean isOnCollection(ElixirRecipe recipe) {
        return collection.stream().anyMatch(holder -> holder.is(recipe));
    }

    public boolean addToCollection(ElixirHolder holder) {
        if (isOnCollection(holder.getRecipe())) return false;
        this.collection.add(holder);
        this.changed = true;
        return true;
    }

    public boolean updateInCollection(ElixirHolder holder) {
        if (!isOnCollection(holder.getRecipe())) return false;
        this.collection.replaceAll(saved -> saved.isSame(holder) ? holder : saved);
        this.changed = true;
        return true;
    }

    public boolean removeFromCollection(ElixirHolder holder) {
        if (!isOnCollection(holder.getRecipe())) return false;
        this.collection.remove(holder);
        this.changed = true;
        return true;
    }

    public void syncWithServer() {
        final var changed = new AtomicBoolean(this.changed);
        final Consumer<ElixirHolder> consumer = holder -> changed.set(true);
        this.getCollection().forEach(holder -> holder.consumeChanges(consumer));
        if (!changed.get()) return;
        this.changed = false;
        Elixirum.PLATFORM.sendToServer(ServerboundProfilePacket.create(packCollection()));
    }

    void handle(ClientboundProfilePacket packet) {
        ClientAlchemy.clearCache();
        this.unpack(packet.content());
    }

    void handle(ClientboundDiscoverPacket packet) {
        this.discoveredEssences.compute(packet.item(), (key, value) -> Util.make(
                value == null ? Sets.newHashSet() : value,
                set -> set.add(packet.essence())));
        this.computeTotalDiscoveredEssences();
    }
}
