package dev.obscuria.elixirum.registry;

import dev.obscuria.core.api.Deferred;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.BiConsumer;

public interface ElixirumAttributes
{
    Deferred<Attribute, RangedAttribute> POTION_MASTERY = register("potion_mastery");
    Deferred<Attribute, RangedAttribute> POTION_IMMUNITY = register("potion_immunity");

    private static Deferred<Attribute, RangedAttribute> register(String name)
    {
        final var description = "attribute.elixirum.%s".formatted(name);
        return ObscureRegistry.register(Elixirum.MODID,
                BuiltInRegistries.ATTRIBUTE,
                Elixirum.key(name),
                () -> new RangedAttribute(description, 0, 0, 3600));
    }

    static void acceptTranslations(BiConsumer<String, String> consumer)
    {
        consumer.accept(POTION_MASTERY.value().getDescriptionId(), "Potion Mastery");
        consumer.accept(POTION_IMMUNITY.value().getDescriptionId(), "Potion Immunity");
    }

    static void init() {}
}
