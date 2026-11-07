package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyProfile;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.*;

public final class ServerAlchemyProfile extends AbstractAlchemyProfile {

    @Getter private final MinecraftServer server;
    @Getter private final UUID uuid;

    public ServerAlchemyProfile(MinecraftServer server, UUID uuid) {
        this.server = server;
        this.uuid = uuid;
    }

    public void sendToClient(Object payload) {
        this.sendToClient(0, payload);
    }

    public void sendToClient(int permissionLevel, Object payload) {
        @Nullable var player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return;
        if (!player.hasPermissions(permissionLevel)) return;
        FragmentumNetworking.sendTo(player, payload);
    }

    public void load() {
        this.unpack(AlchemyCodex.PROFILES.loadEntry(server, uuid.toString()));
    }

    public void save() {
        AlchemyCodex.PROFILES.saveEntry(server, uuid.toString(), pack());
    }
}
