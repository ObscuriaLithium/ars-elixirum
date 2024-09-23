package dev.obscuria.elixirum.platform;

import com.mojang.serialization.MapCodec;
import dev.obscuria.elixirum.network.*;
import dev.obscuria.elixirum.registry.LazyRegister;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
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

public final class FabricPlatform implements IPlatform {

    @Override
    public <TValue> void register(LazyRegister<TValue> registrar) {
        registrar.register();
    }

    @Override
    public <TValue extends BlockEntity> BlockEntityType.Builder<TValue>
    createBlockEntityType(BiFunction<BlockPos, BlockState, TValue> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory::apply, blocks);
    }

    @Override
    public <TValue extends ParticleOptions> ParticleType<TValue>
    createParticleType(boolean alwaysSpawn,
                       MapCodec<TValue> codec,
                       StreamCodec<RegistryFriendlyByteBuf, TValue> streamCodec) {
        return FabricParticleTypes.complex(alwaysSpawn, codec, streamCodec);
    }

    @Override
    public void sendToServer(Object packet) {

    }

    @Override
    public void sendToPlayer(ServerPlayer player, Object packet) {
        if (packet instanceof ClientboundItemEssencesPacket typed)
            ServerPlayNetworking.send(player, new FabricClientboundItemEssencesPayload(typed));
        if (packet instanceof ClientboundProfilePacket typed)
            ServerPlayNetworking.send(player, new FabricClientboundProfilePayload(typed));
        if (packet instanceof ClientboundDiscoverPacket typed)
            ServerPlayNetworking.send(player, new FabricClientboundDiscoverPayload(typed));
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
