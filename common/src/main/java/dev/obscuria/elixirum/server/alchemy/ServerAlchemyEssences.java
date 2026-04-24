package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyEssences;
import dev.obscuria.elixirum.server.alchemy.generation.EssenceGenerator;
import dev.obscuria.elixirum.server.alchemy.generation.EssenceReconciler;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;

import java.util.*;

public final class ServerAlchemyEssences extends AbstractAlchemyEssences {

    ServerAlchemyEssences() {}

    public void load(MinecraftServer server) {
        var packed = AlchemyCodex.ESSENCES.load(server);
        EssenceHolder.unbindAll();
        this.essenceMap.clear();
        this.holderSet.clear();
        packed.essences().forEach(this::register);
    }

    public void save(MinecraftServer server) {
        AlchemyCodex.ESSENCES.save(server, pack());
    }

    public void reconcile() {
        var generator = new EssenceGenerator();
        for (var mobEffect : BuiltInRegistries.MOB_EFFECT.holders().toList()) {
            EssenceReconciler.reconcile(generator, this, mobEffect);
        }
        EssenceHolder.unbindAll();
        this.essenceMap.forEach(this::registerHolder);
    }

    public boolean containsEffect(Holder.Reference<MobEffect> mobEffect) {
        return essenceMap.containsKey(mobEffect.key().location());
    }
}
