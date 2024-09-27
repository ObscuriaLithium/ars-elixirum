package dev.obscuria.elixirum.fabric.platform;

import com.mojang.serialization.MapCodec;
import dev.obscuria.elixirum.fabric.FabricPayload;
import dev.obscuria.elixirum.platform.IPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
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

public final class FabricPlatform implements IPlatform {

    @Override
    public <T> Holder<T> registerReference(Registry<T> registry, ResourceLocation id, Supplier<T> value) {
        return Registry.registerForHolder(registry, id, value.get());
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
    public void sendToServer(Object object) {
        FabricPayload.wrap(object).ifPresent(ClientPlayNetworking::send);
    }

    @Override
    public void sendToPlayer(final ServerPlayer player, Object object) {
        FabricPayload.wrap(object).ifPresent(payload -> ServerPlayNetworking.send(player, payload));
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
