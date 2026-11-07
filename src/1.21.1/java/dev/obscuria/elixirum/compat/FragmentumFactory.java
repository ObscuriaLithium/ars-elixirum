package dev.obscuria.elixirum.compat;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public final class FragmentumFactory {
    public static <T extends ParticleOptions> ParticleType<T> newParticleType(boolean overrideLimiter,
            MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return new ParticleType<>(overrideLimiter) {
            @Override public MapCodec<T> codec() { return codec; }
            @Override public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }
        };
    }

    public static <T extends BlockEntity> BlockEntityType.Builder<T> newBlockEntityType(
            BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::apply, blocks);
    }
}
