package dev.obscuria.elixirum.common.registry;

import com.mojang.serialization.Codec;
import dev.obscuria.archivist.api.v1.components.ComponentKey;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.api.alchemy.EffectProvider;
import dev.obscuria.elixirum.api.IBrewingRecipe;
import dev.obscuria.elixirum.common.alchemy.codex.ProfileComponents;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedEssence;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedIngredient;
import dev.obscuria.elixirum.server.alchemy.resources.PredefinedRecipe;
import dev.obscuria.elixirum.server.alchemy.resources.providers.IFormMethodProvider;
import dev.obscuria.elixirum.server.alchemy.resources.providers.IEssenceProvider;
import dev.obscuria.elixirum.server.alchemy.resources.providers.IFocusProvider;
import dev.obscuria.elixirum.server.alchemy.resources.providers.IRiskProvider;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import dev.obscuria.fragmentum.registry.DelegatedRegistry;
import dev.obscuria.fragmentum.registry.FragmentumRegistry;
import dev.obscuria.fragmentum.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class ElixirumRegistries {

    static final Registrar REGISTRAR = FragmentumRegistry.registrar(ArsElixirum.MODID);

    public static final DelegatedRegistry<IBrewingRecipe> BREWING_RECIPE = REGISTRAR.createRegistry(Keys.BREWING_RECIPE);
    public static final DelegatedRegistry<ComponentKey<?>> PROFILE_COMPONENT_TYPE = REGISTRAR.createRegistry(Keys.PROFILE_COMPONENT_TYPE);
    public static final DelegatedRegistry<Codec<? extends EffectProvider>> EFFECT_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.EFFECT_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends IEssenceProvider>> ESSENCE_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.ESSENCE_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends IFocusProvider>> TEMPER_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.TEMPER_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends IFormMethodProvider>> APPLICATION_METHOD_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.APPLICATION_METHOD_PROVIDER_TYPE);
    public static final DelegatedRegistry<Codec<? extends IRiskProvider>> STABILITY_PROVIDER_TYPE = REGISTRAR.createRegistry(Keys.STABILITY_PROVIDER_TYPE);

    public interface Keys {

        ResourceKey<Registry<IBrewingRecipe>> BREWING_RECIPE = create("brewing_recipe");

        ResourceKey<Registry<ComponentKey<?>>> PROFILE_COMPONENT_TYPE = create("profile_component_type");

        ResourceKey<Registry<Codec<? extends EffectProvider>>> EFFECT_PROVIDER_TYPE = create("effect_provider_type");
        ResourceKey<Registry<Codec<? extends IEssenceProvider>>> ESSENCE_PROVIDER_TYPE = create("essence_provider_type");
        ResourceKey<Registry<Codec<? extends IFocusProvider>>> TEMPER_PROVIDER_TYPE = create("temper_provider_type");
        ResourceKey<Registry<Codec<? extends IFormMethodProvider>>> APPLICATION_METHOD_PROVIDER_TYPE = create("application_method_provider_type");
        ResourceKey<Registry<Codec<? extends IRiskProvider>>> STABILITY_PROVIDER_TYPE = create("stability_provider_type");

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
        ElixirumCreativeTabs.init();
        ElixirumEntities.init();
        ElixirumBlockEntities.init();
        ElixirumParticles.init();
        ElixirumRecipeSerializers.init();

        IBrewingRecipe.bootstrap(BootstrapContext.create(REGISTRAR, Keys.BREWING_RECIPE, ArsElixirum::identifier));
        ProfileComponents.bootstrap(BootstrapContext.create(REGISTRAR, Keys.PROFILE_COMPONENT_TYPE, ArsElixirum::identifier));
        EffectProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.EFFECT_PROVIDER_TYPE, ArsElixirum::identifier));
        IEssenceProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.ESSENCE_PROVIDER_TYPE, ResourceLocation::new));
        IFocusProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.TEMPER_PROVIDER_TYPE, ResourceLocation::new));
        IFormMethodProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.APPLICATION_METHOD_PROVIDER_TYPE, ResourceLocation::new));
        IRiskProvider.bootstrap(BootstrapContext.create(REGISTRAR, Keys.STABILITY_PROVIDER_TYPE, ResourceLocation::new));

        REGISTRAR.createDataRegistry(Keys.PREDEFINED_ESSENCE, () -> PredefinedEssence.DIRECT_CODEC);
        REGISTRAR.createDataRegistry(Keys.PREDEFINED_RECIPE, () -> PredefinedRecipe.DIRECT_CODEC);
        REGISTRAR.createDataRegistry(Keys.PREDEFINED_INGREDIENT, () -> PredefinedIngredient.DIRECT_CODEC);
    }
}
