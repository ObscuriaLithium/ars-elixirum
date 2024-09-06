package dev.obscuria.elixirum.common.effect;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ShrinkMobEffect extends MobEffect {

    public ShrinkMobEffect() {
        super(MobEffectCategory.HARMFUL, 14270531);

        addAttributeModifier(Attributes.SCALE, -0.1);
        addAttributeModifier(Attributes.MAX_HEALTH, -0.1);
        addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, -0.1);
        addAttributeModifier(Attributes.GRAVITY, -0.025);

        addAttributeModifier(Attributes.STEP_HEIGHT, -0.1);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, -0.05);
        addAttributeModifier(Attributes.JUMP_STRENGTH, -0.025);
        addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, -0.05);

        addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, -0.1);
        addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, -0.1);
        addAttributeModifier(Attributes.ATTACK_KNOCKBACK, -0.05);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, -0.05);
        addAttributeModifier(Attributes.ATTACK_SPEED, 0.05);
    }

    private void addAttributeModifier(Holder<Attribute> attribute, double amount) {
        addAttributeModifier(attribute, Elixirum.key("effect.shrink"), amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
