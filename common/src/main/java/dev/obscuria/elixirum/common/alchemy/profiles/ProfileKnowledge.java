package dev.obscuria.elixirum.common.alchemy.profiles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.ElixirumCodecs;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import dev.obscuria.elixirum.common.world.AtomicCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public record ProfileKnowledge(
        Map<Item, IngredientStats> ingredients,
        Map<MobEffect, MobEffectStats> effects
) {

    public static final Codec<ProfileKnowledge> CODEC;

    public static ProfileKnowledge empty() {
        return new ProfileKnowledge(
                new HashMap<>(),
                new HashMap<>());
    }

    public static ProfileKnowledge mutable(
            Map<Item, IngredientStats> ingredients,
            Map<MobEffect, MobEffectStats> effects) {
        return new ProfileKnowledge(
                new HashMap<>(ingredients),
                new HashMap<>(effects));
    }

    public void discoverEssence(Item item, EssenceHolder essence) {
        ingredients.computeIfAbsent(item, IngredientStats::empty).discover(essence);
    }

    public boolean isEssenceKnown(Item item, EssenceHolder essence) {
        if (!ingredients.containsKey(item)) return false;
        return ingredients.get(item).knownEssences.contains(essence);
    }

    public void effectApplied(MobEffect effect) {
        effects.computeIfAbsent(effect, MobEffectStats::empty).applied();
    }

    public boolean isExperienced(MobEffect effect) {
        if (!effects.containsKey(effect)) return false;
        return effects.get(effect).applicationCount.get() > 0;
    }

    static {
        var ingredientsCodec = Codec.unboundedMap(ElixirumCodecs.ITEM_NO_FALLBACK, IngredientStats.CODEC);
        var effectsCodec = Codec.unboundedMap(BuiltInRegistries.MOB_EFFECT.byNameCodec(), MobEffectStats.CODEC);
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                ingredientsCodec.fieldOf("ingredients").forGetter(ProfileKnowledge::ingredients),
                effectsCodec.fieldOf("effects").forGetter(ProfileKnowledge::effects)
        ).apply(codec, ProfileKnowledge::mutable));
    }

    public record IngredientStats(
            List<EssenceHolder> knownEssences,
            AtomicInteger usageCount
    ) {

        public static final Codec<IngredientStats> CODEC;

        public void discover(EssenceHolder essence) {
            if (knownEssences.contains(essence)) return;
            knownEssences.add(essence);
        }

        public static IngredientStats empty(Item item) {
            return new IngredientStats(new ArrayList<>(), new AtomicInteger());
        }

        public static IngredientStats mutable(
                List<EssenceHolder> knownEssences,
                AtomicInteger usageCount) {
            return new IngredientStats(new ArrayList<>(knownEssences), usageCount);
        }

        static {
            CODEC = RecordCodecBuilder.create(codec -> codec.group(
                    EssenceHolder.CODEC.listOf().fieldOf("known_essences").forGetter(IngredientStats::knownEssences),
                    AtomicCodecs.INT.fieldOf("usage_count").forGetter(IngredientStats::usageCount)
            ).apply(codec, IngredientStats::mutable));
        }
    }

    public record MobEffectStats(
            AtomicInteger applicationCount
    ) {

        public static final Codec<MobEffectStats> CODEC;

        public static MobEffectStats empty(MobEffect effect) {
            return new MobEffectStats(new AtomicInteger());
        }

        public void applied() {
            applicationCount.incrementAndGet();
        }

        static {
            CODEC = RecordCodecBuilder.create(codec -> codec.group(
                    AtomicCodecs.INT.fieldOf("application_count").forGetter(MobEffectStats::applicationCount)
            ).apply(codec, MobEffectStats::new));
        }
    }
}
