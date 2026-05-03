package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.AlchemyIngredient;
import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record _AlchemyRecipe(
        Optional<AlchemyIngredient> base,
        Optional<AlchemyIngredient> catalyst,
        Optional<AlchemyIngredient> inhibitor,
        UUID uuid
) implements AlchemyRecipe {

    public static final Codec<AlchemyRecipe> CODEC;
    public static final PayloadCodec<AlchemyRecipe> PAYLOAD_CODEC;
    public static final AlchemyRecipe EMPTY;

    public _AlchemyRecipe(
            Optional<AlchemyIngredient> base,
            Optional<AlchemyIngredient> catalyst,
            Optional<AlchemyIngredient> inhibitor
    ) {
        this(base, catalyst, inhibitor, uuidOf(base, catalyst, inhibitor));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AlchemyRecipe that && uuid.equals(that.uuid());
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    private static UUID uuidOf(
            Optional<AlchemyIngredient> base,
            Optional<AlchemyIngredient> catalyst,
            Optional<AlchemyIngredient> inhibitor
    ) {
        return UUID.nameUUIDFromBytes("%s;%s;%s".formatted(base, catalyst, inhibitor).getBytes());
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AlchemyIngredient.codec().optionalFieldOf("1").forGetter(AlchemyRecipe::base),
                AlchemyIngredient.codec().optionalFieldOf("2").forGetter(AlchemyRecipe::catalyst),
                AlchemyIngredient.codec().optionalFieldOf("3").forGetter(AlchemyRecipe::inhibitor)
        ).apply(codec, _AlchemyRecipe::new));
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(CODEC, FragmentumProxy::registryAccess);
        EMPTY = new _AlchemyRecipe(Optional.empty(), Optional.empty(), Optional.empty());
    }
}
