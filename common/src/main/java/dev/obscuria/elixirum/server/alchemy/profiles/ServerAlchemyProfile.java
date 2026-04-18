package dev.obscuria.elixirum.server.alchemy.profiles;

import dev.obscuria.elixirum.common.alchemy.profiles.*;
import dev.obscuria.elixirum.common.network.ClientboundMasteryLevelUpPayload;
import dev.obscuria.elixirum.common.network.ClientboundMasterySyncPayload;
import dev.obscuria.elixirum.server.alchemy.AlchemyCodex;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.UUID;

public final class ServerAlchemyProfile implements AlchemyProfileView {

    private final MinecraftServer server;
    private final UUID uuid;
    private AlchemyProfileData data = AlchemyProfileData.empty();

    public ServerAlchemyProfile(MinecraftServer server, UUID uuid) {
        this.server = server;
        this.uuid = uuid;
    }

    public void load() {
        this.data = AlchemyCodex.PROFILES.loadEntry(server, uuid.toString());
        this.data.mastery().listener().set(new ProfileMastery.Listener() {

            @Override
            public void onChanged(ProfileMastery mastery) {
                sendToPlayer(new ClientboundMasterySyncPayload(
                        mastery.masteryLevel().get(),
                        mastery.masteryXp().get()));
            }

            @Override
            public void onLevelUp(ProfileMastery mastery) {
                sendToPlayer(new ClientboundMasteryLevelUpPayload(mastery.masteryLevel().get()));
            }

            private void sendToPlayer(Object payload) {
                @Nullable var player = server.getPlayerList().getPlayer(uuid);
                if (player == null) return;
                FragmentumNetworking.sendTo(player, payload);
            }
        });
    }

    public void save() {
        AlchemyCodex.PROFILES.saveEntry(server, uuid.toString(), data);
    }

    public @Nullable CompoundTag pack() {
        return AlchemyProfileData.CODEC
                .encodeStart(NbtOps.INSTANCE, data)
                .map(CompoundTag.class::cast)
                .result().orElse(null);
    }

    @Override
    public ProfileMastery mastery() {
        return data.mastery();
    }

    @Override
    public ProfileKnowledge knowledge() {
        return data.knowledge();
    }

    @Override
    public ProfileCollection collection() {
        return data.collection();
    }

    @Override
    public ProfileStatistics statistics() {
        return data.statistics();
    }
}
