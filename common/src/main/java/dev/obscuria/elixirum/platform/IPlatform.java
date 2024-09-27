package dev.obscuria.elixirum.platform;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IPlatform {

    <T> Holder<T> registerReference(Registry<T> registry, ResourceLocation id, Supplier<T> value);

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

    default boolean isDedicatedServer() {
        return !isClient();
    }
}