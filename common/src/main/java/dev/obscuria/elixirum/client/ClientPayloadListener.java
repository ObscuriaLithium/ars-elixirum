package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.toast.MasteryLevelUpToast;
import dev.obscuria.elixirum.client.screen.toast.NewCapToast;
import dev.obscuria.elixirum.client.screen.toast.NewChromaToast;
import dev.obscuria.elixirum.client.screen.toast.NewShapeToast;
import dev.obscuria.elixirum.common.alchemy.styles.Cap;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.common.alchemy.styles.Shape;
import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import dev.obscuria.elixirum.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class ClientPayloadListener {

    public static void handle(Player player, ClientboundElixirBrewedPayload payload) {
        ClientAlchemy.INSTANCE.justBrewed(payload.recipe());
    }

    public static void handle(Player player, ClientboundAlchemyPayload payload) {
        payload.essences().ifPresent(ClientAlchemy.INSTANCE.essences()::unpack);
        payload.ingredients().ifPresent(ClientAlchemy.INSTANCE.ingredients()::unpack);
        payload.profile().ifPresent(ClientAlchemy.localProfile()::unpack);
    }

    public static void handle(Player player, ClientboundMasterySyncPayload payload) {
        ClientAlchemy.localProfile().mastery()._setLevel(payload.level());
        ClientAlchemy.localProfile().mastery()._setXp(payload.xp());
    }

    public static void handle(Player player, ClientboundDiscoverEssencePayload payload) {
        var profile = ClientAlchemy.INSTANCE.localProfile();
        DiscoverySystem.discoverEssence(profile, payload.item(), payload.essence());
    }

    public static void handle(Player player, ClientboundMasteryLevelUpPayload payload) {
        var toasts = Minecraft.getInstance().getToasts();
        toasts.addToast(new MasteryLevelUpToast(payload.masteryLevel()));
        for (var cap : Cap.values()) {
            if (cap.mastery != payload.masteryLevel()) continue;
            toasts.addToast(new NewCapToast(cap));
        }
        for (var shape : Shape.values()) {
            if (shape.mastery != payload.masteryLevel()) continue;
            toasts.addToast(new NewShapeToast(shape));
        }
        for (var chroma : Chroma.values()) {
            if (chroma.mastery != payload.masteryLevel()) continue;
            toasts.addToast(new NewChromaToast(chroma));
        }
    }
}
