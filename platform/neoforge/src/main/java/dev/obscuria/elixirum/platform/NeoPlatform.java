package dev.obscuria.elixirum.platform;

import com.mojang.serialization.MapCodec;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.network.NeoClientboundItemEssencesPayload;
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
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NeoPlatform implements IPlatform {

    @Override
    public <T> Holder<T> registerReference(Registry<T> registry, ResourceLocation id, Supplier<T> value) {
        return null;
    }

    @Override
    public <TValue extends BlockEntity> BlockEntityType.Builder<TValue>
    createBlockEntityType(BiFunction<BlockPos, BlockState, TValue> factory, Block... blocks) {
        return null;
    }

    @Override
    public <TValue extends ParticleOptions> ParticleType<TValue> createParticleType(boolean alwaysSpawn, MapCodec<TValue> codec, StreamCodec<RegistryFriendlyByteBuf, TValue> streamCodec) {
        return null;
    }

    @Override
    public void sendToServer(Object packet) {}

    @Override
    public void sendToPlayer(ServerPlayer player, Object packet) {
        if (packet instanceof ClientboundItemEssencesPacket typed)
            PacketDistributor.sendToPlayer(player, new NeoClientboundItemEssencesPayload(typed));
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }
}