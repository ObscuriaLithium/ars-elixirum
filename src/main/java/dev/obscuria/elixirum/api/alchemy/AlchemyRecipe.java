package dev.obscuria.elixirum.api.alchemy;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.recipes.ConfiguredRecipe;
import dev.obscuria.elixirum.common.alchemy._AlchemyRecipe;
import dev.obscuria.fragmentum.network.PayloadCodec;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface AlchemyRecipe {

    static Codec<AlchemyRecipe> codec() {
        return _AlchemyRecipe.CODEC;
    }

    static PayloadCodec<AlchemyRecipe> payloadCodec() {
        return _AlchemyRecipe.PAYLOAD_CODEC;
    }

    static AlchemyRecipe empty() {
        return _AlchemyRecipe.EMPTY;
    }

    static AlchemyRecipe create(
            AlchemyIngredient base,
            AlchemyIngredient catalyst,
            AlchemyIngredient inhibitor
    ) {
        return new _AlchemyRecipe(Optional.of(base), Optional.of(catalyst), Optional.of(inhibitor));
    }

    static AlchemyRecipe create(
            Optional<AlchemyIngredient> base,
            Optional<AlchemyIngredient> catalyst,
            Optional<AlchemyIngredient> inhibitor
    ) {
        return new _AlchemyRecipe(base, catalyst, inhibitor);
    }

    Optional<AlchemyIngredient> base();

    Optional<AlchemyIngredient> catalyst();

    Optional<AlchemyIngredient> inhibitor();

    UUID uuid();

    default ConfiguredRecipe configure() {
        return new ConfiguredRecipe(this);
    }

    default boolean isEmpty() {
        return base().isEmpty() && catalyst().isEmpty() && inhibitor().isEmpty();
    }

    default boolean isComplete() {
        return base().isPresent() && catalyst().isPresent() && inhibitor().isPresent();
    }

    default AlchemyRecipe withBase(AlchemyIngredient ingredient) {
        return create(Optional.of(ingredient), catalyst(), inhibitor());
    }

    default AlchemyRecipe withCatalyst(AlchemyIngredient ingredient) {
        return create(base(), Optional.of(ingredient), inhibitor());
    }

    default AlchemyRecipe withInhibitor(AlchemyIngredient ingredient) {
        return create(base(), catalyst(), Optional.of(ingredient));
    }

    default AlchemyRecipe append(AlchemyIngredient ingredient) {
        if (base().isEmpty()) return withBase(ingredient);
        if (catalyst().isEmpty()) return withCatalyst(ingredient);
        if (inhibitor().isEmpty()) return withInhibitor(ingredient);
        return this;
    }
}
