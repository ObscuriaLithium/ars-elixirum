package dev.obscuria.elixirum.common.world.block;

import com.google.common.collect.ImmutableList;
import dev.obscuria.elixirum.client.sounds.CauldronSoundInstance;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface GlassCauldronBehavior {

    ImmutableList<GlassCauldronBehavior> QUEUE = buildBehaviors();

    boolean shouldTick(GlassCauldronEntity entity);

    boolean tick(GlassCauldronEntity entity, Level level, BlockPos pos, BlockState state);

    private static ImmutableList<GlassCauldronBehavior> buildBehaviors() {
        final var builder = ImmutableList.<GlassCauldronBehavior>builder();
        builder.add(new CoolingBehavior());
        builder.add(new HeatingBehavior());
        builder.add(new BoilingBehavior());
        return builder.build();
    }

    final class CoolingBehavior implements GlassCauldronBehavior {

        @Override
        public boolean shouldTick(GlassCauldronEntity entity) {
            return entity.isFilled && !entity.hasHeatSource() && entity.temperature > 0.0;
        }

        @Override
        public boolean tick(GlassCauldronEntity entity, Level level, BlockPos pos, BlockState state) {
            entity.shiftTemperature(-0.002);
            if (level.isClientSide) {
                if (level.random.nextFloat() <= 0.2f * entity.temperature) {
                    entity.createSplashParticles(1);
                }
            }
            return true;
        }
    }

    final class HeatingBehavior implements GlassCauldronBehavior {

        @Override
        public boolean shouldTick(GlassCauldronEntity entity) {
            return entity.isFilled && entity.hasHeatSource() && entity.temperature < 1.0;
        }

        @Override
        public boolean tick(GlassCauldronEntity entity, Level level, BlockPos pos, BlockState state) {
            entity.shiftTemperature(0.002);
            if (level.isClientSide) {
                CauldronSoundInstance.maybePlayFor(entity);
                if (level.random.nextFloat() <= 0.2f * entity.temperature) {
                    entity.createSplashParticles(1);
                }
            }
            return true;
        }
    }

    final class BoilingBehavior implements GlassCauldronBehavior {

        @Override
        public boolean shouldTick(GlassCauldronEntity entity) {
            return entity.isFilled && entity.hasHeatSource() && entity.temperature >= 1.0;
        }

        @Override
        public boolean tick(GlassCauldronEntity entity, Level level, BlockPos pos, BlockState state) {
            if (level.isClientSide()) {
                CauldronSoundInstance.maybePlayFor(entity);
                entity.createSplashParticles(1);
                entity.createBubbleParticles(1);
            }
            return true;
        }
    }
}
