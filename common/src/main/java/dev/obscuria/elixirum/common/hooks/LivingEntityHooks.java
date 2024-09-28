package dev.obscuria.elixirum.common.hooks;

import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.registry.ElixirumItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;

public final class LivingEntityHooks {

    public static boolean checkTotemDeathProtection(LivingEntity entity, DamageSource source) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        for (var hand : InteractionHand.values()) {
            final var stack = entity.getItemInHand(hand);
            if (!stack.is(ElixirumItems.WITCH_TOTEM_OF_UNDYING.asItem())) continue;

            final var totem = stack.copy();
            stack.shrink(1);
            if (entity instanceof ServerPlayer player) {
                player.awardStat(Stats.ITEM_USED.get(ElixirumItems.WITCH_TOTEM_OF_UNDYING.asItem()), 1);
                CriteriaTriggers.USED_TOTEM.trigger(player, totem);
                entity.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            }

            entity.setHealth(1.0F);
            entity.removeAllEffects();
            ElixirContents.get(totem).apply(entity, entity, entity);
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
            entity.level().broadcastEntityEvent(entity, EntityEvent.TALISMAN_ACTIVATE);
            return true;
        }
        return false;
    }
}
