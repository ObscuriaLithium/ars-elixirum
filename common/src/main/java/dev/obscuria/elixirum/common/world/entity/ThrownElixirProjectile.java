package dev.obscuria.elixirum.common.world.entity;

import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.traits.Form;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.common.registry.ElixirumEntities;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public final class ThrownElixirProjectile extends ThrowableItemProjectile {

    public ThrownElixirProjectile(EntityType<? extends ThrownElixirProjectile> type, Level level) {
        super(type, level);
    }

    public ThrownElixirProjectile(Level level, Player player) {
        super(ElixirumEntities.THROWN_ELIXIR.get(), player, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ElixirumItems.ELIXIR.asItem();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide) {
            final var contents = ArsElixirumAPI.getElixirContents(getItem());
            final var direction = hitResult.getDirection();
            final var pos = hitResult.getBlockPos().relative(direction);
            if (contents.isEmpty()) {
                this.dowseFire(pos);
                this.dowseFire(pos.relative(direction.getOpposite()));
                for (var horizontalDirection : Direction.Plane.HORIZONTAL)
                    this.dowseFire(pos.relative(horizontalDirection));
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            var contents = ArsElixirumAPI.getElixirContents(getItem());
            if (contents.isEmpty()) {
                this.applyWater();
            } else if (contents.form() == Form.EXPLOSIVE) {
                @Nullable var target = hitResult instanceof EntityHitResult result ? result.getEntity() : null;
                this.applySplash(contents, target);
            } else if (contents.form() == Form.LINGERING) {
                this.makeAreaOfEffectCloud(getItem(), contents);
            }
            this.level().levelEvent(contents.hasInstantEffects()
                            ? LevelEvent.PARTICLES_INSTANT_POTION_SPLASH
                            : LevelEvent.PARTICLES_SPELL_POTION_SPLASH,
                    blockPosition(), contents.color().decimal());
            this.discard();
        }
    }

    private void applyWater() {
        final var area = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        for (var entity : this.level().getEntitiesOfClass(LivingEntity.class, area, ThrownPotion.WATER_SENSITIVE_OR_ON_FIRE)) {
            final var distance = this.distanceToSqr(entity);
            if (distance < 16.0D) {
                if (entity.isSensitiveToWater())
                    entity.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                if (entity.isOnFire() && entity.isAlive())
                    entity.extinguishFire();
            }
        }
        for (Axolotl axolotl : this.level().getEntitiesOfClass(Axolotl.class, area))
            axolotl.rehydrate();
    }

    private void applySplash(ElixirContents contents, @Nullable Entity entity) {
        final var area = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
        final var targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
        if (!targets.isEmpty()) {
            final var source = this.getEffectSource();
            for (var target : targets) {
                if (target.isAffectedByPotions()) {
                    final var distance = this.distanceToSqr(target);
                    if (distance < 16.0D) {
                        final var scale = target == entity ? 1D : 1D - Math.sqrt(distance) / 4D;
                        contents.scale(scale).apply(target, this, source);
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack stack, ElixirContents contents) {
        var cloud = new AreaEffectCloud(level(), getX(), getY(), getZ());
        if (getOwner() instanceof LivingEntity owner) cloud.setOwner(owner);

        cloud.setRadius(3.0F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.setPotion(Potions.EMPTY);

        cloud.setFixedColor(contents.color().decimal());
        contents.forEachEffect(cloud::addEffect);
        this.level().addFreshEntity(cloud);
    }

    private void dowseFire(BlockPos pos) {
        var state = this.level().getBlockState(pos);
        if (state.is(BlockTags.FIRE)) {
            this.level().destroyBlock(pos, false, this);
        } else if (AbstractCandleBlock.isLit(state)) {
            AbstractCandleBlock.extinguish(null, state, this.level(), pos);
        } else if (CampfireBlock.isLitCampfire(state)) {
            this.level().levelEvent(null, 1009, pos, 0);
            CampfireBlock.dowse(this.getOwner(), this.level(), pos, state);
            this.level().setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, Boolean.FALSE));
        }
    }
}
