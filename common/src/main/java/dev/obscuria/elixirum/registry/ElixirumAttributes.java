package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Supplier;

public interface ElixirumAttributes {
    LazyRegister<Attribute> REGISTER = LazyRegister.create(BuiltInRegistries.ATTRIBUTE, Elixirum.MODID);

    LazyHolder<Attribute, Attribute> POTION_MASTERY = simple("potion_mastery",
            () -> new RangedAttribute("attribute.elixirum.potion_mastery", 0, 0, 3600));
    LazyHolder<Attribute, Attribute> POTION_IMMUNITY = simple("potion_immunity",
            () -> new RangedAttribute("attribute.elixirum.potion_immunity", 0, 0, 3600));

    private static LazyHolder<Attribute, Attribute>
    simple(final String name, Supplier<Attribute> supplier) {
        return REGISTER.register(name, supplier);
    }
}
