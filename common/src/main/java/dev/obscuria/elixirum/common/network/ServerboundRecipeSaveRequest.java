package dev.obscuria.elixirum.common.network;

import dev.obscuria.elixirum.common.alchemy.profiles.ConfiguredRecipe;
import dev.obscuria.elixirum.server.ServerPayloadListener;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundRecipeSaveRequest(ConfiguredRecipe recipe) {

    public static final PayloadCodec<ServerboundRecipeSaveRequest> CODEC;

    public static void encode(ServerboundRecipeSaveRequest request, FriendlyByteBuf byteBuf) {
        CODEC.write(byteBuf, request);
    }

    public static ServerboundRecipeSaveRequest decode(FriendlyByteBuf byteBuf) {
        return CODEC.read(byteBuf);
    }

    public static void handle(ServerPlayer player, ServerboundRecipeSaveRequest request) {
        ServerPayloadListener.handle(player, request);
    }

    static {
        CODEC = PayloadCodec.registryFriendly(
                ConfiguredRecipe.CODEC.xmap(
                        ServerboundRecipeSaveRequest::new,
                        ServerboundRecipeSaveRequest::recipe),
                FragmentumProxy::registryAccess);
    }
}
