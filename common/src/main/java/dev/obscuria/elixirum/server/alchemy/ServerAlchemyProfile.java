package dev.obscuria.elixirum.server.alchemy;

import dev.obscuria.archivist.api.v1.components.ComponentMap;
import dev.obscuria.elixirum.common.alchemy.codex.AbstractAlchemyProfile;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.*;

public final class ServerAlchemyProfile extends AbstractAlchemyProfile {

    @Getter private final ComponentMap components;
    @Getter private final MinecraftServer server;
    @Getter private final UUID uuid;

    public ServerAlchemyProfile(MinecraftServer server, UUID uuid) {
        this.components = ComponentMap.empty();
        this.server = server;
        this.uuid = uuid;
    }

    public void sendToClient(Object payload) {
        @Nullable var player = server.getPlayerList().getPlayer(uuid);
        if (player == null) return;
        FragmentumNetworking.sendTo(player, payload);
    }

    public void load() {
        this.unpack(AlchemyCodex.PROFILES.loadEntry(server, uuid.toString()));
    }

    public void save() {
        AlchemyCodex.PROFILES.saveEntry(server, uuid.toString(), pack());
    }
}
