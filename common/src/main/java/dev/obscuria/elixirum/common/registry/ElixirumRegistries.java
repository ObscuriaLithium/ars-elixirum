package dev.obscuria.elixirum.common.registry;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.alchemy.basics.EffectProvider;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedEssence;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedIngredient;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedRecipe;
import dev.obscuria.elixirum.server.alchemy.resources.providers.IEssenceProvider;
import dev.obscuria.elixirum.server.alchemy.resources.providers.ICatalystPropertyProvider;
import dev.obscuria.elixirum.server.alchemy.resources.providers.IFoundationPropertyProvider;
import dev.obscuria.elixirum.server.alchemy.resources.providers.IStabilizerPropertyProvider;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import dev.obscuria.fragmentum.registry.DelegatedRegistry;
import dev.obscuria.fragmentum.registry.FragmentumRegistry;
import dev.obscuria.fragmentum.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class ElixirumRegistries {

    static final Registrar REGISTRAR = FragmentumRegistry.registrar(ArsElixirum.MODID);

    public static final DelegatedRegistry<Codec<? extends EffectProvider>> EFFECT_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.EFFECT_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends IEssenceProvider>> ESSENCE_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.ESSENCE_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends IFoundationPropertyProvider>> FOUNDATION_PROPERTY_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.FOUNDATION_PROPERTY_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends ICatalystPropertyProvider>> CATALYST_PROPERTY_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.CATALYST_PROPERTY_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends IStabilizerPropertyProvider>> STABILIZER_PROPERTY_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.STABILIZER_PROPERTY_PROVIDER_TYPE);

    public interface Keys {

        ResourceKey<Registry<Codec<? extends EffectProvider>>> EFFECT_PROVIDER_TYPE = create("effect_provider_type");
        ResourceKey<Registry<Codec<? extends IEssenceProvider>>> ESSENCE_PROVIDER_TYPE = create("essence_provider_type");
        ResourceKey<Registry<Codec<? extends IFoundationPropertyProvider>>> FOUNDATION_PROPERTY_PROVIDER_TYPE = create("foundation_property_provider_type");
        ResourceKey<Registry<Codec<? extends ICatalystPropertyProvider>>> CATALYST_PROPERTY_PROVIDER_TYPE = create("catalyst_property_provider_type");
        ResourceKey<Registry<Codec<? extends IStabilizerPropertyProvider>>> STABILIZER_PROPERTY_PROVIDER_TYPE = create("stabilizer_property_provider_type");

        ResourceKey<Registry<PredefinedEssence>> PREDEFINED_ESSENCE = create("predefined_essence");
        ResourceKey<Registry<PredefinedRecipe>> PREDEFINED_RECIPE = create("predefined_recipe");
        ResourceKey<Registry<PredefinedIngredient>> PREDEFINED_INGREDIENT = create("predefined_ingredient");

        private static <T> ResourceKey<Registry<T>> create(String name) {
            return ResourceKey.createRegistryKey(ArsElixirum.identifier(name));
        }
    }

    public static void init() {

        ElixirumSounds.init();
        ElixirumAttributes.init();
        ElixirumItems.init();
        ElixirumBlocks.init();
        ElixirumBlockEntities.init();
        ElixirumCreativeTabs.init();
        ElixirumParticles.init();

        EffectProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.EFFECT_PROVIDER_TYPE, ArsElixirum::identifier));
        IEssenceProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.ESSENCE_PROVIDER_TYPE, ResourceLocation::new));
        IFoundationPropertyProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.FOUNDATION_PROPERTY_PROVIDER_TYPE, ResourceLocation::new));
        ICatalystPropertyProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.CATALYST_PROPERTY_PROVIDER_TYPE, ResourceLocation::new));
        IStabilizerPropertyProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.STABILIZER_PROPERTY_PROVIDER_TYPE, ResourceLocation::new));

        REGISTRAR.createDataRegistry(Keys.PREDEFINED_ESSENCE, () -> PredefinedEssence.DIRECT_CODEC);
        REGISTRAR.createDataRegistry(Keys.PREDEFINED_RECIPE, () -> PredefinedRecipe.DIRECT_CODEC);
        REGISTRAR.createDataRegistry(Keys.PREDEFINED_INGREDIENT, () -> PredefinedIngredient.DIRECT_CODEC);
    }
}
