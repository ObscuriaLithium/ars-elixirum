package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.BiConsumer;

public enum ElixirumAttributes {
    POTION_MASTERY("potion_mastery"),
    POTION_IMMUNITY("potion_immunity");

    private final Holder<Attribute> holder;

    ElixirumAttributes(String name) {
        final var description = "attribute.elixirum.%s".formatted(name);
        this.holder = Elixirum.PLATFORM.registerReference(
                BuiltInRegistries.ATTRIBUTE, Elixirum.key(name),
                () -> new RangedAttribute(description, 0, 0, 3600));
    }

    public Holder<Attribute> holder() {
        return this.holder;
    }

    public Attribute value() {
        return this.holder.value();
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(POTION_MASTERY.value().getDescriptionId(), "Potion Mastery");
        consumer.accept(POTION_IMMUNITY.value().getDescriptionId(), "Potion Immunity");
    }

    public static void init() {}
}
