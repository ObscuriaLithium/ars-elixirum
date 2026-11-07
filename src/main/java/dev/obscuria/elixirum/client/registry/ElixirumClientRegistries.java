package dev.obscuria.elixirum.client.registry;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.screen.alchemy.guide.contents.ContentBlock;
import dev.obscuria.fragmentum.registry.BootstrapContext;
import dev.obscuria.fragmentum.registry.DelegatedRegistry;
import dev.obscuria.fragmentum.registry.FragmentumRegistry;
import dev.obscuria.fragmentum.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class ElixirumClientRegistries {

    static final Registrar REGISTRAR = FragmentumRegistry.registrar(ArsElixirum.MODID);

    public static final DelegatedRegistry<Codec<? extends ContentBlock>> CONTENT_BLOCK_TYPE = REGISTRAR.createRegistry(Keys.CONTENT_BLOCK_TYPE);

    public interface Keys {

        ResourceKey<Registry<Codec<? extends ContentBlock>>> CONTENT_BLOCK_TYPE = create("content_block_type");

        private static <T> ResourceKey<Registry<T>> create(String name) {
            return ResourceKey.createRegistryKey(ArsElixirum.identifier(name));
        }
    }

    public static void init() {

        ContentBlock.bootstrap(BootstrapContext.create(REGISTRAR, Keys.CONTENT_BLOCK_TYPE, ResourceLocation::new));
    }
}
