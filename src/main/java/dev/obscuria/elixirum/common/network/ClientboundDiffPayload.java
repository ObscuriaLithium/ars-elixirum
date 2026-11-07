package dev.obscuria.elixirum.common.network;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.ClientPayloadListener;
import dev.obscuria.elixirum.common.alchemy.Diff;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record ClientboundDiffPayload(
        Optional<Diff> essenceGenerationResult,
        Optional<Diff> ingredientGenerationResult
) {

    public static final PayloadCodec<ClientboundDiffPayload> PAYLOAD_CODEC;

    public ClientboundDiffPayload(Diff essences, Diff ingredients) {
        this(Optional.of(essences), Optional.of(ingredients));
    }

    public static ClientboundDiffPayload forEssences(Diff essences) {
        return new ClientboundDiffPayload(Optional.of(essences), Optional.empty());
    }

    public static ClientboundDiffPayload forIngredients(Diff ingredients) {
        return new ClientboundDiffPayload(Optional.empty(), Optional.of(ingredients));
    }

    public static void encode(ClientboundDiffPayload payload, FriendlyByteBuf byteBuf) {
        PAYLOAD_CODEC.write(byteBuf, payload);
    }

    public static ClientboundDiffPayload decode(FriendlyByteBuf byteBuf) {
        return PAYLOAD_CODEC.read(byteBuf);
    }

    public static void handle(Player player, ClientboundDiffPayload payload) {
        ClientPayloadListener.handle(player, payload);
    }

    static {
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(RecordCodecBuilder.create(codec -> codec.group(
                Diff.CODEC.optionalFieldOf("essences").forGetter(ClientboundDiffPayload::essenceGenerationResult),
                Diff.CODEC.optionalFieldOf("ingredients").forGetter(ClientboundDiffPayload::ingredientGenerationResult)
        ).apply(codec, ClientboundDiffPayload::new)), FragmentumProxy::registryAccess);
    }
}
