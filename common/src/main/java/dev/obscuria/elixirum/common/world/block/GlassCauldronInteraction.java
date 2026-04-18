package dev.obscuria.elixirum.common.world.block;

import com.google.common.collect.ImmutableList;
import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.api.Alchemy;
import dev.obscuria.elixirum.common.network.ClientboundElixirBrewedPayload;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public interface GlassCauldronInteraction {

    ImmutableList<GlassCauldronInteraction> EMPTY = buildEmptyInteractions();
    ImmutableList<GlassCauldronInteraction> ELIXIR = buildElixirInteractions();

    boolean shouldInteract(ItemStack stack);

    InteractionResult interact(GlassCauldronEntity entity, Level level, ItemStack stack, Player player, InteractionHand hand);

    private static ImmutableList<GlassCauldronInteraction> buildEmptyInteractions() {
        final var builder = ImmutableList.<GlassCauldronInteraction>builder();
        builder.add(new EmptyFillWater());
        return builder.build();
    }

    private static ImmutableList<GlassCauldronInteraction> buildElixirInteractions() {
        final var builder = ImmutableList.<GlassCauldronInteraction>builder();
        builder.add(new ElixirFlush());
        builder.add(new ElixirScoopUp());
        builder.add(new ElixirAddIngredient());
        return builder.build();
    }

    final class EmptyFillWater implements GlassCauldronInteraction {

        public boolean shouldInteract(ItemStack stack) {
            return stack.is(Items.WATER_BUCKET);
        }

        public InteractionResult interact(GlassCauldronEntity entity, Level level, ItemStack stack, Player player, InteractionHand hand) {
            if (!entity.maybeFillWater()) return InteractionResult.PASS;
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, Items.BUCKET.getDefaultInstance()));
                player.awardStat(Stats.FILL_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                entity.playSound(SoundEvents.BUCKET_EMPTY, 1f, 1f);
                level.gameEvent(null, GameEvent.FLUID_PLACE, entity.getBlockPos());
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }

    final class ElixirFlush implements GlassCauldronInteraction {

        public boolean shouldInteract(ItemStack stack) {
            return stack.is(Items.BUCKET);
        }

        public InteractionResult interact(GlassCauldronEntity entity, Level level, ItemStack stack, Player player, InteractionHand hand) {
            if (!entity.maybeFlush()) return InteractionResult.PASS;
            if (!level.isClientSide()) {
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                entity.playSound(SoundEvents.BUCKET_FILL, 1f, 1f);
                level.gameEvent(null, GameEvent.FLUID_PICKUP, entity.getBlockPos());
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }

    final class ElixirScoopUp implements GlassCauldronInteraction {

        public boolean shouldInteract(ItemStack stack) {
            return stack.is(Items.GLASS_BOTTLE);
        }

        public InteractionResult interact(GlassCauldronEntity entity, Level level, ItemStack stack, Player player, InteractionHand hand) {
            var recipe = entity.recipe();
            var result = entity.scoopUp();
            player.playSound(SoundEvents.BOTTLE_FILL, 1f, 1f);
            if (level instanceof ServerLevel serverLevel && !result.isEmpty()) {
                stack.shrink(1);
                var profile = ServerAlchemy.get(serverLevel.getServer()).profileOf(player);
                profile.mastery().grandXp(recipe.uuid(), 1);
                profile.statistics().brewed(recipe);
                profile.collection().findConfig(recipe.uuid()).ifPresent(config -> {
                    ArsElixirumHelper.setStyle(result, config.getStyle());
                    ArsElixirumHelper.setChroma(result, config.getChroma());
                });
                if (!player.addItem(result)) {
                    player.drop(result, false);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    FragmentumNetworking.sendTo(serverPlayer, new ClientboundElixirBrewedPayload(recipe));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }

    final class ElixirAddIngredient implements GlassCauldronInteraction {

        public boolean shouldInteract(ItemStack stack) {
            return !Alchemy.guess().ingredients().propertiesOf(stack).isEmpty();
        }

        public InteractionResult interact(GlassCauldronEntity entity, Level level, ItemStack stack, Player player, InteractionHand hand) {
            if (!level.isClientSide && entity.appendIngredient(stack)) {
                stack.shrink(1);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }
    }
}
