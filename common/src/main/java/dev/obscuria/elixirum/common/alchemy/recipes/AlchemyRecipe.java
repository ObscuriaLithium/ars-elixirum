package dev.obscuria.elixirum.common.alchemy.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyIngredient;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("all")
public final class AlchemyRecipe {

    public static final Codec<AlchemyRecipe> CODEC;
    public static final PayloadCodec<AlchemyRecipe> PAYLOAD_CODEC;
    public static final AlchemyRecipe EMPTY;

    @Getter private final Optional<AlchemyIngredient> base;
    @Getter private final Optional<AlchemyIngredient> catalyst;
    @Getter private final Optional<AlchemyIngredient> inhibitor;
    @Getter private final transient UUID uuid;

    public AlchemyRecipe(
            Optional<AlchemyIngredient> base,
            Optional<AlchemyIngredient> catalyst,
            Optional<AlchemyIngredient> inhibitor) {
        this.base = base;
        this.catalyst = catalyst;
        this.inhibitor = inhibitor;
        this.uuid = createUuid();
    }

    public ConfiguredRecipe configure() {
        return new ConfiguredRecipe(this);
    }

    public boolean isEmpty() {
        return base.isEmpty() && catalyst.isEmpty() && inhibitor.isEmpty();
    }

    public boolean isComplete() {
        return base.isPresent() && catalyst.isPresent() && inhibitor.isPresent();
    }

    public AlchemyRecipe withBase(AlchemyIngredient ingredient) {
        return new AlchemyRecipe(Optional.of(ingredient), catalyst, inhibitor);
    }

    public AlchemyRecipe withCatalyst(AlchemyIngredient ingredient) {
        return new AlchemyRecipe(base, Optional.of(ingredient), inhibitor);
    }

    public AlchemyRecipe withInhibitor(AlchemyIngredient ingredient) {
        return new AlchemyRecipe(base, catalyst, Optional.of(ingredient));
    }

    public AlchemyRecipe append(AlchemyIngredient alchemyIngredient) {
        if (base.isEmpty()) return withBase(alchemyIngredient);
        if (catalyst.isEmpty()) return withCatalyst(alchemyIngredient);
        if (inhibitor.isEmpty()) return withInhibitor(alchemyIngredient);
        return this;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AlchemyRecipe that && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public UUID createUuid() {
        return UUID.nameUUIDFromBytes("%s;%s;%s".formatted(base, catalyst, inhibitor).getBytes());
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                AlchemyIngredient.CODEC.optionalFieldOf("1").forGetter(AlchemyRecipe::getBase),
                AlchemyIngredient.CODEC.optionalFieldOf("2").forGetter(AlchemyRecipe::getCatalyst),
                AlchemyIngredient.CODEC.optionalFieldOf("3").forGetter(AlchemyRecipe::getInhibitor)
        ).apply(codec, AlchemyRecipe::new));
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(CODEC, FragmentumProxy::registryAccess);
        EMPTY = new AlchemyRecipe(Optional.empty(), Optional.empty(), Optional.empty());
    }
}
