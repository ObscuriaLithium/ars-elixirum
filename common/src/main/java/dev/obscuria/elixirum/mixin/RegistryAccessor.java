package dev.obscuria.elixirum.mixin;

import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltInRegistries.class)
public interface RegistryAccessor {

    @Accessor("WRITABLE_REGISTRY")
    static WritableRegistry<WritableRegistry<?>> getRoot() {
        throw new UnsupportedOperationException();
    }
}
