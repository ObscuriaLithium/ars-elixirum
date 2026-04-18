package dev.obscuria.elixirum.server.alchemy.generation;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.alchemy.AlchemyEssencesData;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedEssence;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public enum EssenceReconciler {
    SKIP_IF_EXISTS(EssenceReconciler::skipIfExists),
    SKIP_IF_IGNORED(EssenceReconciler::skipIfIgnored),
    GENERATE_PREDEFINED(EssenceReconciler::generatePredefined),
    GENERATE_RANDOM(EssenceReconciler::generateRandom);

    private final Action action;

    EssenceReconciler(Action action) {
        this.action = action;
    }

    public static void reconcile(EssenceGenerator generator, AlchemyEssencesData essences, Holder.Reference<MobEffect> mobEffect) {
        for (var chain : values()) {
            if (chain.action.reconcile(generator, essences, mobEffect)) return;
        }
    }

    private static boolean skipIfExists(EssenceGenerator generator, AlchemyEssencesData essences, Holder.Reference<MobEffect> mobEffect) {
        return essences.containsEffect(mobEffect);
    }

    private static boolean skipIfIgnored(EssenceGenerator generator, AlchemyEssencesData essences, Holder.Reference<MobEffect> mobEffect) {
        return mobEffect.is(ArsElixirum.IGNORED_EFFECTS);
    }

    private static boolean generatePredefined(EssenceGenerator generator, AlchemyEssencesData essences, Holder.Reference<MobEffect> mobEffect) {
        return PredefinedEssence.findFor(mobEffect).map(predefined -> {
            var key = predefined.value().essence().effect().unwrapKey().orElseThrow().location();
            essences.put(key, predefined.value().essence());
            return true;
        }).orElse(false);
    }

    private static boolean generateRandom(EssenceGenerator generator, AlchemyEssencesData essences, Holder.Reference<MobEffect> mobEffect) {
        return generator.generateEssence(mobEffect).map(essence -> {
            essences.put(mobEffect.key().location(), essence);
            return true;
        }).orElse(false);
    }

    @FunctionalInterface
    public interface Action {

        boolean reconcile(EssenceGenerator generator, AlchemyEssencesData essences, Holder.Reference<MobEffect> mobEffect);
    }
}
