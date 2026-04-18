package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.elixirum.common.alchemy.AlchemyEssencesData;
import dev.obscuria.elixirum.api.AlchemyEssencesView;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import dev.obscuria.elixirum.server.alchemy.generation.EssenceGenerator;
import dev.obscuria.elixirum.server.alchemy.generation.EssenceReconciler;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;
import java.util.Map;

public final class ServerAlchemyEssences implements AlchemyEssencesView {

    private AlchemyEssencesData data = AlchemyEssencesData.empty();

    ServerAlchemyEssences() {}

    void load(MinecraftServer server) {
        data = AlchemyCodex.ESSENCES.load(server);
        data.rebindHolders();
    }

    void save(MinecraftServer server) {
        AlchemyCodex.ESSENCES.save(server, data);
    }

    void reconcile() {
        var generator = new EssenceGenerator();
        BuiltInRegistries.MOB_EFFECT.holders().forEach(it -> EssenceReconciler.reconcile(generator, data, it));
        data.rebindHolders();
    }

    @Nullable CompoundTag pack() {
        return AlchemyEssencesData.CODEC
                .encodeStart(NbtOps.INSTANCE, data)
                .map(CompoundTag.class::cast)
                .result().orElse(null);
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
