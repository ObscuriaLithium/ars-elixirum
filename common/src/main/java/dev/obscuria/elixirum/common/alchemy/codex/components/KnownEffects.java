package dev.obscuria.elixirum.common.alchemy.codex.components;

import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class KnownEffects {

    public static final Codec<KnownEffects> CODEC;

    @Getter public final Set<MobEffect> effects;

    public static KnownEffects empty() {
        return new KnownEffects(Set.of());
    }

    public KnownEffects(Set<MobEffect> effects) {
        this.effects = new HashSet<>(effects);
    }

    private KnownEffects(List<MobEffect> effects) {
        this(new HashSet<>(effects));
    }

    public boolean isKnown(MobEffect effect) {
        return effects.contains(effect);
    }

    public boolean discover(MobEffect effect) {
        return effects.add(effect);
    }

    private List<MobEffect> effectList() {
        return List.copyOf(effects);
    }

    static {
        CODEC = BuiltInRegistries.MOB_EFFECT.byNameCodec().listOf()
                .xmap(KnownEffects::new, KnownEffects::effectList);
    }
}
