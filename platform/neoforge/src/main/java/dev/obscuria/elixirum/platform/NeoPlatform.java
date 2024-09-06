package dev.obscuria.elixirum.platform;

import com.google.common.collect.Sets;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.network.NeoClientboundItemEssencesPayload;
import dev.obscuria.elixirum.registry.LazyRegister;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public class NeoPlatform implements IPlatform {
    public static final Set<LazyRegister<?>> registers = Sets.newHashSet();

    @Override
    public <TValue> void register(LazyRegister<TValue> register) {
        registers.add(register);
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