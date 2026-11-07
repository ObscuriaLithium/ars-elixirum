package dev.obscuria.elixirum.server.alchemy.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.AlchemyProperties;
import dev.obscuria.elixirum.common.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.alchemy.resources.providers.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;

import java.util.Optional;

public record PredefinedIngredient(
        HolderSet<Item> items,
        Optional<IEssenceProvider> essenceProvider,
        Optional<IFocusProvider> focusProvider,
        Optional<IFormMethodProvider> formProvider,
        Optional<IRiskProvider> riskProvider
) {

    public static final PredefinedIngredient EMPTY;
    public static final Codec<Holder<PredefinedIngredient>> CODEC;
    public static final Codec<PredefinedIngredient> DIRECT_CODEC;

    @SuppressWarnings("deprecation")
    public boolean isFor(Item item) {
        return items.contains(item.builtInRegistryHolder());
    }

    public AlchemyProperties resolve(Item item, RandomSource random) {
        var propertyProvider = this.essenceProvider.orElse(GeneratedEssenceProvider.SHARED);
        var essences = propertyProvider.resolve(item, random.fork());
        var aspect = propertyProvider.resolveAspect(essences);
        return AlchemyProperties.create(aspect, essences,
                focusProvider
                        .orElse(GeneratedFocusProvider.SHARED)
                        .resolve(item, random.fork()),
                formProvider
                        .orElse(GeneratedFormMethodProvider.SHARED)
                        .resolve(item, random.fork()),
                riskProvider
                        .orElse(GeneratedRiskProvider.SHARED)
                        .resolve(item, random.fork()));
    }

    public static Optional<PredefinedIngredient> findFor(Item item, RegistryAccess registryAccess) {
        return registryAccess.lookupOrThrow(ElixirumRegistries.Keys.PREDEFINED_INGREDIENT).listElements()
                .filter(it -> it.value().isFor(item))
                .map(Holder::value).findFirst();
    }

    static {
        CODEC = RegistryFixedCodec.create(ElixirumRegistries.Keys.PREDEFINED_INGREDIENT);
        DIRECT_CODEC = RecordCodecBuilder.create(codec -> codec.group(
                RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(PredefinedIngredient::items),
                IEssenceProvider.CODEC.optionalFieldOf("essences").forGetter(PredefinedIngredient::essenceProvider),
                IFocusProvider.CODEC.optionalFieldOf("focus").forGetter(PredefinedIngredient::focusProvider),
                IFormMethodProvider.CODEC.optionalFieldOf("form").forGetter(PredefinedIngredient::formProvider),
                IRiskProvider.CODEC.optionalFieldOf("risk").forGetter(PredefinedIngredient::riskProvider)
        ).apply(codec, PredefinedIngredient::new));
        EMPTY = new PredefinedIngredient(
                HolderSet.direct(),
                Optional.of(GeneratedEssenceProvider.SHARED),
                Optional.of(GeneratedFocusProvider.SHARED),
                Optional.of(GeneratedFormMethodProvider.SHARED),
                Optional.of(GeneratedRiskProvider.SHARED));
    }
}
