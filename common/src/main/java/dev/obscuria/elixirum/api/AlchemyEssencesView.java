package dev.obscuria.elixirum.api;

import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Map;

public interface AlchemyEssencesView {

    EssenceHolder get(ResourceLocation key);

    EssenceHolder asHolder(Essence essence);

    boolean containsEffect(Holder.Reference<MobEffect> effect);

    Map<ResourceLocation, Essence> asMapView();
}
