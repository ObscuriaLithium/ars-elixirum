package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Supplier;

public interface ElixirumAttributes {
    LazyRegister<Attribute> SOURCE = LazyRegister.create(BuiltInRegistries.ATTRIBUTE, Elixirum.MODID);

    LazyValue<Attribute, Attribute> POTION_MASTERY = simple("potion_mastery",
            () -> new RangedAttribute("attribute.elixirum.potion_mastery", 0, 0, 3600));
    LazyValue<Attribute, Attribute> POTION_IMMUNITY = simple("potion_immunity",
            () -> new RangedAttribute("attribute.elixirum.potion_immunity", 0, 0, 3600));

    private static LazyValue<Attribute, Attribute>
    simple(final String name, Supplier<Attribute> supplier) {
        return SOURCE.register(name, supplier);
    }
}
