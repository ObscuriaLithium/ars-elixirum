package dev.obscuria.elixirum.common.alchemy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ElixirumProfile {
    public static final Codec<ElixirumProfile.Packed> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirumProfile.Packed> STREAM_CODEC;

    protected final List<ElixirHolder> savedPages = Lists.newArrayList();
    protected final Map<Item, Set<Holder<Essence>>> discoveredEssences = Maps.newHashMap();
    private int totalDiscoveredEssences;

    public int getTotalDiscoveredEssences() {
        return this.totalDiscoveredEssences;
    }

    public Packed pack() {
        return new Packed(
                ImmutableList.copyOf(savedPages),
                ImmutableMap.copyOf(discoveredEssences));
    }

    public void unpack(Packed packed) {
        savedPages.clear();
        savedPages.addAll(packed.savedPages);
        discoveredEssences.clear();
        discoveredEssences.putAll(packed.discoveredEssences);
        this.computeTotalDiscoveredEssences();
    }

    protected void computeTotalDiscoveredEssences() {
        this.totalDiscoveredEssences = discoveredEssences.values().stream().mapToInt(Set::size).sum();
    }

    public record Packed(@Unmodifiable List<ElixirHolder> savedPages,
                         @Unmodifiable Map<Item, Set<Holder<Essence>>> discoveredEssences) { }

    static {
        final var setCodec = Essence.CODEC.listOf().xmap(ElixirumProfile::setFactory, ElixirumProfile::listFactory);
        final var mapCodec = Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), setCodec);
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ElixirHolder.CODEC.listOf().fieldOf("saved_pages").forGetter(Packed::savedPages),
                mapCodec.fieldOf("discovered_essences").forGetter(Packed::discoveredEssences)
        ).apply(instance, Packed::new));
        final var setStreamCodec = Essence.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ElixirumProfile::setFactory, ElixirumProfile::listFactory);
        final var mapStreamCodec = ByteBufCodecs.map(ElixirumProfile::mapFactory, ByteBufCodecs.registry(Registries.ITEM), setStreamCodec);
        STREAM_CODEC = StreamCodec.composite(
                ElixirHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), Packed::savedPages,
                mapStreamCodec, Packed::discoveredEssences,
                Packed::new);
    }

    private static Map<Item, Set<Holder<Essence>>> mapFactory(int size) {
        return new HashMap<>(size);
    }

    private static <V> Set<V> setFactory(List<V> list) {
        return Sets.newHashSet(list);
    }

    private static <V> List<V> listFactory(Set<V> set) {
        return Lists.newArrayList(set.iterator());
    }
}
