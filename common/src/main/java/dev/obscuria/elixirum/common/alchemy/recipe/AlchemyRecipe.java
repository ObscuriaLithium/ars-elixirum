package dev.obscuria.elixirum.common.alchemy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyIngredient;
import dev.obscuria.elixirum.common.alchemy.profiles.ConfiguredRecipe;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;

import java.util.Optional;
import java.util.UUID;

public record AlchemyRecipe(
        Optional<AlchemyIngredient> foundation,
        Optional<AlchemyIngredient> catalyst,
        Optional<AlchemyIngredient> stabilizer
) {

    public static final Codec<AlchemyRecipe> CODEC;
    public static final PayloadCodec<AlchemyRecipe> PAYLOAD_CODEC;
    public static final AlchemyRecipe EMPTY;

    public ConfiguredRecipe save() {
        return new ConfiguredRecipe(this);
    }

    public boolean isEmpty() {
        return foundation.isEmpty() && catalyst.isEmpty() && stabilizer.isEmpty();
    }

    public boolean isComplete() {
        return foundation.isPresent() && catalyst.isPresent() && stabilizer.isPresent();
    }

    public boolean isSame(AlchemyRecipe other) {
        return isSame(other.uuid());
    }

    public boolean isSame(UUID other) {
        return uuid().equals(other);
    }

    public AlchemyRecipe withFoundation(AlchemyIngredient ingredient) {
        return new AlchemyRecipe(Optional.of(ingredient), catalyst, stabilizer);
    }

    public AlchemyRecipe withCatalyst(AlchemyIngredient ingredient) {
        return new AlchemyRecipe(foundation, Optional.of(ingredient), stabilizer);
    }

    public AlchemyRecipe withStabilizer(AlchemyIngredient ingredient) {
        return new AlchemyRecipe(foundation, catalyst, Optional.of(ingredient));
    }

    public AlchemyRecipe append(AlchemyIngredient alchemyIngredient) {
        if (foundation.isEmpty()) return withFoundation(alchemyIngredient);
        if (catalyst.isEmpty()) return withCatalyst(alchemyIngredient);
        if (stabilizer.isEmpty()) return withStabilizer(alchemyIngredient);
        return this;
    }

    public UUID uuid() {
        return UUID.nameUUIDFromBytes("%s;%s;%s".formatted(foundation, catalyst, stabilizer).getBytes());
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AlchemyIngredient.CODEC.optionalFieldOf("foundation").forGetter(AlchemyRecipe::foundation),
                AlchemyIngredient.CODEC.optionalFieldOf("catalyst").forGetter(AlchemyRecipe::catalyst),
                AlchemyIngredient.CODEC.optionalFieldOf("stabilizer").forGetter(AlchemyRecipe::stabilizer)
        ).apply(codec, AlchemyRecipe::new));
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(CODEC, FragmentumProxy::registryAccess);
        EMPTY = new AlchemyRecipe(Optional.empty(), Optional.empty(), Optional.empty());
    }
}
