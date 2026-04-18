package dev.obscuria.elixirum.server.alchemy.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
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
        Optional<IEssenceProvider> propertyProvider,
        Optional<IFoundationPropertyProvider> foundationProvider,
        Optional<ICatalystPropertyProvider> catalystProvider,
        Optional<IStabilizerPropertyProvider> stabilizerProvider
) {

    public static final PredefinedIngredient EMPTY;
    public static final Codec<Holder<PredefinedIngredient>> CODEC;
    public static final Codec<PredefinedIngredient> DIRECT_CODEC;

    @SuppressWarnings("deprecation")
    public boolean isFor(Item item) {
        return items.contains(item.builtInRegistryHolder());
    }

    public AlchemyProperties resolve(Item item, RandomSource random) {
        var propertyProvider = this.propertyProvider.orElse(GeneratedEssenceProvider.SHARED);
        var essences = propertyProvider.resolve(item, random.fork());
        var aspect = propertyProvider.resolveAspect(essences);
        return new AlchemyProperties(aspect, essences,
                foundationProvider
                        .orElse(GeneratedFoundationPropertyProvider.SHARED)
                        .resolve(item, random.fork()),
                catalystProvider
                        .orElse(GeneratedCatalystPropertyProvider.SHARED)
                        .resolve(item, random.fork()),
                stabilizerProvider
                        .orElse(GeneratedStabilizerPropertyProvider.SHARED)
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
                IEssenceProvider.CODEC.optionalFieldOf("properties").forGetter(PredefinedIngredient::propertyProvider),
                IFoundationPropertyProvider.CODEC.optionalFieldOf("foundation").forGetter(PredefinedIngredient::foundationProvider),
                ICatalystPropertyProvider.CODEC.optionalFieldOf("catalyst").forGetter(PredefinedIngredient::catalystProvider),
                IStabilizerPropertyProvider.CODEC.optionalFieldOf("stabilizer").forGetter(PredefinedIngredient::stabilizerProvider)
        ).apply(codec, PredefinedIngredient::new));
        EMPTY = new PredefinedIngredient(
                HolderSet.direct(),
                Optional.of(GeneratedEssenceProvider.SHARED),
                Optional.of(GeneratedFoundationPropertyProvider.SHARED),
                Optional.of(GeneratedCatalystPropertyProvider.SHARED),
                Optional.of(GeneratedStabilizerPropertyProvider.SHARED));
    }
}
