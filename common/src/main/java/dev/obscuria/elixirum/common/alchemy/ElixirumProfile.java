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

import java.util.*;

public abstract class ElixirumProfile
{
    public static final Codec<ElixirumProfile.Packed> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirumProfile.Packed> STREAM_CODEC;

    protected final List<ElixirHolder> collection = Lists.newArrayList();
    protected final Map<Item, Set<Holder<Essence>>> discoveredEssences = Maps.newHashMap();
    private int totalDiscoveredEssences;

    public int getTotalDiscoveredEssences()
    {
        return this.totalDiscoveredEssences;
    }

    public Packed pack()
    {
        return new Packed(
                Optional.of(ImmutableList.copyOf(collection)),
                Optional.of(ImmutableMap.copyOf(discoveredEssences)));
    }

    public Packed packCollection()
    {
        return new Packed(
                Optional.of(ImmutableList.copyOf(collection)),
                Optional.empty());
    }

    public void unpack(Packed packed)
    {
        packed.collection.ifPresent(collection -> {
            this.collection.clear();
            this.collection.addAll(collection);
        });
        packed.discoveredEssences.ifPresent(discoveredEssences -> {
            this.discoveredEssences.clear();
            this.discoveredEssences.putAll(discoveredEssences);
            this.computeTotalDiscoveredEssences();
        });
    }

    public void unpackCollection(Packed packed)
    {
        packed.collection.ifPresent(collection -> {
            this.collection.clear();
            this.collection.addAll(collection);
        });
    }

    protected void computeTotalDiscoveredEssences()
    {
        this.totalDiscoveredEssences = discoveredEssences.values().stream().mapToInt(Set::size).sum();
    }

    public record Packed(@Unmodifiable Optional<List<ElixirHolder>> collection,
                         @Unmodifiable Optional<Map<Item, Set<Holder<Essence>>>> discoveredEssences) {}

    static
    {
        final var setCodec = Essence.CODEC.listOf().xmap(ElixirumProfile::setFactory, ElixirumProfile::listFactory);
        final var mapCodec = Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), setCodec);
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ElixirHolder.CODEC.listOf().optionalFieldOf("collection").forGetter(Packed::collection),
                mapCodec.optionalFieldOf("discovered_essences").forGetter(Packed::discoveredEssences)
        ).apply(instance, Packed::new));
        final var setStreamCodec = Essence.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ElixirumProfile::setFactory, ElixirumProfile::listFactory);
        final var mapStreamCodec = ByteBufCodecs.map(ElixirumProfile::mapFactory, ByteBufCodecs.registry(Registries.ITEM), setStreamCodec);
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ElixirHolder.STREAM_CODEC.apply(ByteBufCodecs.list())), Packed::collection,
                ByteBufCodecs.optional(mapStreamCodec), Packed::discoveredEssences,
                Packed::new);
    }

    private static Map<Item, Set<Holder<Essence>>> mapFactory(int size)
    {
        return new HashMap<>(size);
    }

    private static <V> Set<V> setFactory(List<V> list)
    {
        return Sets.newHashSet(list);
    }

    private static <V> List<V> listFactory(Set<V> set)
    {
        return Lists.newArrayList(set.iterator());
    }
}
