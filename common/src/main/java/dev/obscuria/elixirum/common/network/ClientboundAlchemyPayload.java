package dev.obscuria.elixirum.common.network;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.ClientPayloadListener;
import dev.obscuria.elixirum.common.alchemy.codex.PackedAlchemyEssences;
import dev.obscuria.elixirum.common.alchemy.codex.PackedAlchemyIngredients;
import dev.obscuria.elixirum.common.alchemy.codex.PackedAlchemyProfile;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record ClientboundAlchemyPayload(
        Optional<PackedAlchemyEssences> essences,
        Optional<PackedAlchemyIngredients> ingredients,
        Optional<PackedAlchemyProfile> profile
) {

    public static final PayloadCodec<ClientboundAlchemyPayload> PAYLOAD_CODEC;

    public static void encode(ClientboundAlchemyPayload payload, FriendlyByteBuf byteBuf) {
        PAYLOAD_CODEC.write(byteBuf, payload);
    }

    public static ClientboundAlchemyPayload decode(FriendlyByteBuf byteBuf) {
        return PAYLOAD_CODEC.read(byteBuf);
    }

    public static void handle(Player player, ClientboundAlchemyPayload payload) {
        ClientPayloadListener.handle(player, payload);
    }

    static {
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(RecordCodecBuilder.create(codec -> codec.group(
                PackedAlchemyEssences.CODEC.optionalFieldOf("essences").forGetter(ClientboundAlchemyPayload::essences),
                PackedAlchemyIngredients.CODEC.optionalFieldOf("ingredients").forGetter(ClientboundAlchemyPayload::ingredients),
                PackedAlchemyProfile.CODEC.optionalFieldOf("profile").forGetter(ClientboundAlchemyPayload::profile)
        ).apply(codec, ClientboundAlchemyPayload::new)), FragmentumProxy::registryAccess);
    }
}
