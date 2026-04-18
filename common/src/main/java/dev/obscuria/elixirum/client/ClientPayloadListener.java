package dev.obscuria.elixirum.client;

import com.mojang.datafixers.util.Pair;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.toast.MasteryLevelUpToast;
import dev.obscuria.elixirum.client.screen.toast.NewCapToast;
import dev.obscuria.elixirum.client.screen.toast.NewChromaToast;
import dev.obscuria.elixirum.client.screen.toast.NewShapeToast;
import dev.obscuria.elixirum.common.alchemy.AlchemyEssencesData;
import dev.obscuria.elixirum.common.alchemy.AlchemyIngredientsData;
import dev.obscuria.elixirum.common.alchemy.profiles.AlchemyProfileData;
import dev.obscuria.elixirum.common.alchemy.style.Cap;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.Shape;
import dev.obscuria.elixirum.common.network.ClientboundAlchemyPayload;
import dev.obscuria.elixirum.common.network.ClientboundElixirBrewedPayload;
import dev.obscuria.elixirum.common.network.ClientboundMasteryLevelUpPayload;
import dev.obscuria.elixirum.common.network.ClientboundMasterySyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;

public final class ClientPayloadListener {

    public static void handle(Player player, ClientboundElixirBrewedPayload payload) {
        ClientAlchemy.INSTANCE.justBrewed(payload.recipe());
    }

    public static void handle(Player player, ClientboundAlchemyPayload payload) {
        if (payload.essencesTag() != null) AlchemyEssencesData.CODEC
                .decode(NbtOps.INSTANCE, payload.essencesTag())
                .map(Pair::getFirst).result()
                .ifPresent(ClientAlchemy.INSTANCE::updateEssences);
        if (payload.ingredientsTag() != null) AlchemyIngredientsData.CODEC
                .decode(NbtOps.INSTANCE, payload.ingredientsTag())
                .map(Pair::getFirst).result()
                .ifPresent(ClientAlchemy.INSTANCE::updateIngredients);
        if (payload.profileTag() != null) AlchemyProfileData.CODEC
                .decode(NbtOps.INSTANCE, payload.profileTag())
                .map(Pair::getFirst).result()
                .ifPresent(ClientAlchemy.INSTANCE::updateProfile);
    }

    public static void handle(Player player, ClientboundMasterySyncPayload payload) {
        var mastery = ClientAlchemy.INSTANCE.localProfile().mastery();
        mastery.masteryLevel().set(payload.masteryLevel());
        mastery.masteryXp().set(payload.masteryXp());
    }

    public static void handle(Player player, ClientboundMasteryLevelUpPayload payload) {
        Minecraft.getInstance().getToasts().addToast(new MasteryLevelUpToast(payload.masteryLevel()));
        for (var cap : Cap.values()) {
            if (cap.mastery != payload.masteryLevel()) continue;
            Minecraft.getInstance().getToasts().addToast(new NewCapToast(cap));
        }
        for (var shape : Shape.values()) {
            if (shape.mastery != payload.masteryLevel()) continue;
            Minecraft.getInstance().getToasts().addToast(new NewShapeToast(shape));
        }
        for (var chroma : Chroma.values()) {
            if (chroma.mastery != payload.masteryLevel()) continue;
            Minecraft.getInstance().getToasts().addToast(new NewChromaToast(chroma));
        }
    }
}
