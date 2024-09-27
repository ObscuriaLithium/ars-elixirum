package dev.obscuria.elixirum.common.alchemy.ingredient;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.affix.Affix;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IngredientProperties {
    public static final Codec<IngredientProperties> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientProperties> STREAM_CODEC;
    public static final IngredientProperties EMPTY = new IngredientProperties(Map.of(), List.of());
    private final Object2IntMap<ResourceLocation> essences;
    private final ImmutableList<Affix> affixes;

    public static IngredientProperties create(Map<ResourceLocation, Integer> essences, List<Affix> affixes) {
        return new IngredientProperties(essences, affixes);
    }

    public static IngredientProperties single(Holder.Reference<Essence> essence, int weight) {
        return new IngredientProperties(Map.of(essence.key().location(), weight), List.of());
    }

    private IngredientProperties(Map<ResourceLocation, Integer> essences, List<Affix> affixes) {
        this.essences = new Object2IntOpenHashMap<>(essences);
        this.affixes = ImmutableList.copyOf(affixes);
    }

    public boolean isEmpty() {
        return this.essences.isEmpty();
    }

    public Object2IntMap<ResourceLocation> getEssences() {
        return new Object2IntOpenHashMap<>(this.essences);
    }

    public Object2IntMap<Holder<Essence>> getEssences(HolderGetter<Essence> getter) {
        final var result = new Object2IntOpenHashMap<Holder<Essence>>();
        for (var entry : this.essences.object2IntEntrySet()) {
            final var essence = getter.getOrThrow(Essence.key(entry.getKey()));
            result.put(essence, entry.getIntValue());
        }
        return result;
    }

    @Contract(pure = true)
    public @Unmodifiable List<Affix> getAffixes() {
        return List.copyOf(affixes);
    }

    public boolean contains(ResourceLocation essence) {
        return this.essences.containsKey(essence);
    }

    public int getWeight(ResourceLocation essence) {
        return this.essences.getOrDefault(essence, 0);
    }

    public IngredientProperties with(ResourceLocation essence, int weight) {
        var result = this.getEssences();
        result.put(essence, weight);
        return create(result, affixes);
    }

    public IngredientProperties exclude(ResourceLocation essence) {
        var result = this.getEssences();
        result.removeInt(essence);
        return create(result, affixes);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("essences").forGetter(IngredientProperties::getEssences),
                Affix.CODEC.listOf().fieldOf("affixes").forGetter(IngredientProperties::getAffixes)
        ).apply(instance, IngredientProperties::create));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT), IngredientProperties::getEssences,
                Affix.STREAM_CODEC.apply(ByteBufCodecs.list()), IngredientProperties::getAffixes,
                IngredientProperties::create);
    }
}
