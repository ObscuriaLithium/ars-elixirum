package dev.obscuria.elixirum.client.alchemy;

import dev.obscuria.elixirum.api.AlchemyEssencesView;
import dev.obscuria.elixirum.common.alchemy.AlchemyEssencesData;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Map;

public final class ClientAlchemyEssences implements AlchemyEssencesView {

    private AlchemyEssencesData data = AlchemyEssencesData.empty();

    ClientAlchemyEssences() {}

    public void update(AlchemyEssencesData data) {
        this.data = data;
        this.data.rebindHolders();
    }

    @Override
    public EssenceHolder get(ResourceLocation key) {
        return EssenceHolder.getOrCreate(key);
    }

    @Override
    public EssenceHolder asHolder(Essence essence) {
        return data.keyOf(essence).map(EssenceHolder::getOrCreate).orElseThrow();
    }

    @Override
    public boolean containsEffect(Holder.Reference<MobEffect> effect) {
        return data.containsEffect(effect);
    }

    @Override
    public Map<ResourceLocation, Essence> asMapView() {
        return data.asMapView();
    }
}
