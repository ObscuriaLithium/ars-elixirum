package dev.obscuria.elixirum.server.alchemy.resources;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

public record PredefinedEssence(
        Essence essence
) {

    public static final Codec<PredefinedEssence> DIRECT_CODEC;

    public boolean isFor(Holder.Reference<MobEffect> mobEffect) {
        return essence.effect().is(mobEffect.key());
    }

    public static Optional<Holder.Reference<PredefinedEssence>> findFor(Holder.Reference<MobEffect> mobEffect) {
        return FragmentumProxy.registryAccess().lookupOrThrow(ElixirumRegistries.Keys.PREDEFINED_ESSENCE).listElements()
                .filter(it -> it.value().isFor(mobEffect))
                .findFirst();
    }

    static {
        DIRECT_CODEC = Essence.CODEC.xmap(PredefinedEssence::new, PredefinedEssence::essence);
    }
}
