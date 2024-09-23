package dev.obscuria.elixirum.platform;

import com.mojang.serialization.MapCodec;
import dev.obscuria.elixirum.registry.LazyRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public interface IPlatform {

    <TValue> void register(LazyRegister<TValue> registrar);

    <TValue extends BlockEntity> BlockEntityType.Builder<TValue>
    createBlockEntityType(BiFunction<BlockPos, BlockState, TValue> factory, Block... blocks);

    <TValue extends ParticleOptions> ParticleType<TValue>
    createParticleType(boolean alwaysSpawn,
                       MapCodec<TValue> codec,
                       StreamCodec<RegistryFriendlyByteBuf, TValue> streamCodec);

    void sendToServer(Object packet);

    void sendToPlayer(ServerPlayer player, Object packet);

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    boolean isClient();
}