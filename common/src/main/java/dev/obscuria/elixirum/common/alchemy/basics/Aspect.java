package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.registry.ElixirumAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Aspect implements StringRepresentable {
    NONE(null, null),
    CORPUS(ElixirumAttributes.CORPUS_MASTERY, ElixirumAttributes.CORPUS_RESISTANCE),       // (🏺Corporeal) – body, health
    ANIMA(ElixirumAttributes.ANIMA_MASTERY, ElixirumAttributes.ANIMA_RESISTANCE),          // (✨Spiritual) – spirit, emotions, inner strength
    VENENUM(ElixirumAttributes.VENENUM_MASTERY, ElixirumAttributes.VENENUM_RESISTANCE),    // (☠️Venomous) – toxicity, danger
    MEDELA(ElixirumAttributes.MEDELA_MASTERY, ElixirumAttributes.MEDELA_RESISTANCE),       // (🌿Healing) – regeneration, purification
    CRESCERE(ElixirumAttributes.CRESCERE_MASTERY, ElixirumAttributes.CRESCERE_RESISTANCE), // (🌱Growth) – improvement, evolution
    MUTATIO(ElixirumAttributes.MUTATIO_MASTERY, ElixirumAttributes.MUTATIO_RESISTANCE),    // (🔄Mutable) – instability, mutations
    FORTUNA(ElixirumAttributes.FORTUNA_MASTERY, ElixirumAttributes.FORTUNA_RESISTANCE),    // (🍀Fateful) – luck, chance
    TENEBRAE(ElixirumAttributes.TENEBRAE_MASTERY, ElixirumAttributes.TENEBRAE_RESISTANCE); // (🕸Dark) – hidden, otherworldly forces

    public static final Codec<Aspect> CODEC = StringRepresentable.fromEnum(Aspect::values);
    public final TagKey<Item> itemTag = createTag(Registries.ITEM);
    public final TagKey<MobEffect> effectTag = createTag(Registries.MOB_EFFECT);
    public final ResourceLocation texture = resolveTexture();
    public final @Nullable Attribute masteryAttribute;
    public final @Nullable Attribute resistanceAttribute;

    Aspect(@Nullable Attribute mastery, @Nullable Attribute resistance) {
        this.masteryAttribute = mastery;
        this.resistanceAttribute = resistance;
    }

    public static Aspect select(Holder<MobEffect> effect) {
        for (var aspect : Aspect.values()) {
            if (!effect.is(aspect.effectTag)) continue;
            return aspect;
        }
        return Aspect.TENEBRAE;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    private <T> TagKey<T> createTag(ResourceKey<Registry<T>> registryKey) {
        final var path = "aspect/" + getSerializedName();
        return TagKey.create(registryKey, ArsElixirum.identifier(path));
    }

    private ResourceLocation resolveTexture() {
        return ArsElixirum.identifier("textures/gui/aspect/%s.png".formatted(getSerializedName()));
    }
}
