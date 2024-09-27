package dev.obscuria.elixirum.common.alchemy.elixir;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.PackedEffect;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record ElixirContents(ImmutableList<PackedEffect> effects, int color) implements TooltipProvider {
    public static final Codec<ElixirContents> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirContents> STREAM_CODEC;
    public static final ElixirContents WATER = new ElixirContents(List.of(), Elixirum.WATER_COLOR);
    private static final Component NO_EFFECT;

    public static Builder create() {
        return new Builder();
    }

    public static Builder create(ElixirContents byOther) {
        return new Builder(byOther);
    }

    public ElixirContents(List<PackedEffect> effects, int color) {
        this(ImmutableList.copyOf(effects), color);
    }

    public ElixirContents(ImmutableList<PackedEffect> effects, int color) {
        this.effects = ImmutableList.copyOf(effects.stream()
                .sorted(Comparator.comparingInt(PackedEffect::getQuality).reversed())
                .toList());
        this.color = color;
    }

    public static ElixirContents get(ItemStack stack) {
        return stack.getOrDefault(ElixirumDataComponents.ELIXIR_CONTENTS, WATER);
    }

    public static Optional<ElixirContents> getOptional(ItemStack stack) {
        return Optional.ofNullable(stack.get(ElixirumDataComponents.ELIXIR_CONTENTS));
    }

    public static int getOverlayColor(ItemStack stack, int layer) {
        return layer != 1 ? -1 : ElixirContents.get(stack).color();
    }

    public static void setRarityByContent(ItemStack stack) {
        if (!stack.has(ElixirumDataComponents.ELIXIR_CONTENTS)) return;
        var contents = get(stack);
        if (contents.isEmpty()) return;
        final var quality = contents.effects().getFirst().getQuality();
        if (quality >= 100) {
            stack.set(DataComponents.RARITY, Rarity.EPIC);
        } else if (quality >= 90) {
            stack.set(DataComponents.RARITY, Rarity.RARE);
        } else if (quality >= 80) {
            stack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        } else {
            stack.set(DataComponents.RARITY, Rarity.COMMON);
        }
    }

    public boolean isEmpty() {
        return this.effects.isEmpty();
    }

    public boolean hasInstantEffects() {
        return effects.stream().anyMatch(PackedEffect::isInstantenous);
    }

    public int getQuality() {
        return this.effects.stream().mapToInt(PackedEffect::getQuality).sum();
    }

    public ElixirContents split(int count) {
        return this.scale(1.0 / count);
    }

    public ElixirContents scale(double scale) {
        return new ElixirContents(scale != 1.0
                ? effects().stream().map(effect -> effect.scale(scale)).toList()
                : effects(), this.color);
    }

    public void apply(LivingEntity target, @Nullable Entity direct, @Nullable Entity source) {
        final var mastery = Elixirum.getPotionMastery(source);
        final var immunity = Elixirum.getPotionImmunity(target);
        this.effects().stream()
                .filter(effect -> !effect.isWeak() && !effect.isPale())
                .map(effect -> effect.instantiate(mastery, immunity))
                .forEach(instance -> {
                    final var effect = instance.getEffect().value();
                    if (effect.isInstantenous()) {
                        effect.applyInstantenousEffect(direct, source, target, instance.getAmplifier(), 1);
                    } else {
                        target.addEffect(instance);
                    }
                });
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        if (this.isEmpty()) {
            consumer.accept(NO_EFFECT);
            return;
        }
        final var attributes = Lists.<Pair<Holder<Attribute>, AttributeModifier>>newArrayList();
        var weakEffects = 0;
        var paleEffects = 0;
        for (var effect : this.effects()) {
            if (effect.isWeak()) {
                weakEffects += 1;
            } else if (effect.isPale()) {
                paleEffects += 1;
            } else {
                consumer.accept(Component.translatable("potion.withDuration",
                                effect.getDisplayName(),
                                effect.getStatusOrDuration(context.tickRate()))
                        .withStyle(effect.getColor()));
            }
        }
        if (weakEffects > 0)
            consumer.accept(Component
                    .translatable("elixir.collapsed.weak", weakEffects)
                    .withStyle(ChatFormatting.GRAY));
        if (paleEffects > 0)
            consumer.accept(Component
                    .translatable("elixir.collapsed.pale", weakEffects)
                    .withStyle(ChatFormatting.GRAY));
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PackedEffect.CODEC.listOf().fieldOf("effects").forGetter(ElixirContents::effects),
                Codec.INT.fieldOf("color").forGetter(ElixirContents::color)
        ).apply(instance, ElixirContents::new));
        STREAM_CODEC = StreamCodec.composite(
                PackedEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), ElixirContents::effects,
                ByteBufCodecs.INT, ElixirContents::color,
                ElixirContents::new);

        NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);
    }

    public static class Builder {
        private final List<PackedEffect> effects = Lists.newArrayList();
        private int color = Elixirum.WATER_COLOR;

        public Builder(ElixirContents byOther) {
            this.effects.addAll(byOther.effects);
            this.color = byOther.color;
        }

        public Builder() {}

        public Builder addEffect(PackedEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public Builder setCustomColor(int color) {
            this.color = color;
            return this;
        }

        public Builder computeContentColor() {
            if (effects.isEmpty()) {
                this.color = Elixirum.WATER_COLOR;
                return this;
            }

            final var colorWeights = effects.stream()
                    .map(effect -> Pair.of(effect.getEssence().getEffect(), effect.getQuality()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
            final int totalWeight = colorWeights.values().stream().reduce(0, Integer::sum);

            float red = 0, green = 0, blue = 0;
            for (Map.Entry<MobEffect, Integer> entry : colorWeights.entrySet()) {
                final var color = entry.getKey().getColor();
                final var weight = entry.getValue();
                final var weightRatio = weight / 1f / totalWeight;
                red += FastColor.ARGB32.red(color) / 255f * weightRatio;
                green += FastColor.ARGB32.green(color) / 255f * weightRatio;
                blue += FastColor.ARGB32.blue(color) / 255f * weightRatio;
            }

            this.color = FastColor.ARGB32.colorFromFloat(1f, red, green, blue);
            return this;
        }

        public ElixirContents build() {
            return new ElixirContents(ImmutableList.copyOf(this.effects), this.color);
        }
    }
}
