package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public interface ElixirumAttributes {

    Attribute POTION_MASTERY = register("generic.potion_mastery", 0, -1024, 1024);
    Attribute POTION_RESISTANCE = register("generic.potion_resistance", 0, -1024, 1024);

    Attribute CORPUS_MASTERY = register("generic.corpus_mastery", 0, -1024, 1024);
    Attribute CORPUS_RESISTANCE = register("generic.corpus_resistance", 0, -1024, 1024);

    Attribute ANIMA_MASTERY = register("generic.anima_mastery", 0, -1024, 1024);
    Attribute ANIMA_RESISTANCE = register("generic.anima_resistance", 0, -1024, 1024);

    Attribute VENENUM_MASTERY = register("generic.venenum_mastery", 0, -1024, 1024);
    Attribute VENENUM_RESISTANCE = register("generic.venenum_resistance", 0, -1024, 1024);

    Attribute MEDELA_MASTERY = register("generic.medela_mastery", 0, -1024, 1024);
    Attribute MEDELA_RESISTANCE = register("generic.medela_resistance", 0, -1024, 1024);

    Attribute CRESCERE_MASTERY = register("generic.crescere_mastery", 0, -1024, 1024);
    Attribute CRESCERE_RESISTANCE = register("generic.crescere_resistance", 0, -1024, 1024);

    Attribute MUTATIO_MASTERY = register("generic.mutatio_mastery", 0, -1024, 1024);
    Attribute MUTATIO_RESISTANCE = register("generic.mutatio_resistance", 0, -1024, 1024);

    Attribute FORTUNA_MASTERY = register("generic.fortuna_mastery", 0, -1024, 1024);
    Attribute FORTUNA_RESISTANCE = register("generic.fortuna_resistance", 0, -1024, 1024);

    Attribute TENEBRAE_MASTERY = register("generic.tenebrae_mastery", 0, -1024, 1024);
    Attribute TENEBRAE_RESISTANCE = register("generic.tenebrae_resistance", 0, -1024, 1024);

    private static Attribute register(String name, int defaultValue, int minValue, int maxValue) {
        var attribute = new RangedAttribute("attribute.elixirum.%s".formatted(name), defaultValue, minValue, maxValue);
        //? if >=1.21 {
        /*return ElixirumRegistries.REGISTRAR.registerAttribute(ArsElixirum.identifier(name), () -> attribute);
        *///? } else {
        ElixirumRegistries.REGISTRAR.registerAttribute(ArsElixirum.identifier(name), () -> attribute);
        return attribute;
        //? }
    }

    static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder addTo(net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder builder) {
        return builder
                .add(ElixirumAttributes.POTION_MASTERY)
                .add(ElixirumAttributes.POTION_RESISTANCE)
                .add(ElixirumAttributes.CORPUS_MASTERY)
                .add(ElixirumAttributes.CORPUS_RESISTANCE)
                .add(ElixirumAttributes.ANIMA_MASTERY)
                .add(ElixirumAttributes.ANIMA_RESISTANCE)
                .add(ElixirumAttributes.VENENUM_MASTERY)
                .add(ElixirumAttributes.VENENUM_RESISTANCE)
                .add(ElixirumAttributes.MEDELA_MASTERY)
                .add(ElixirumAttributes.MEDELA_RESISTANCE)
                .add(ElixirumAttributes.CRESCERE_MASTERY)
                .add(ElixirumAttributes.CRESCERE_RESISTANCE)
                .add(ElixirumAttributes.MUTATIO_MASTERY)
                .add(ElixirumAttributes.MUTATIO_RESISTANCE)
                .add(ElixirumAttributes.FORTUNA_MASTERY)
                .add(ElixirumAttributes.FORTUNA_RESISTANCE)
                .add(ElixirumAttributes.TENEBRAE_MASTERY)
                .add(ElixirumAttributes.TENEBRAE_RESISTANCE);
    }

    static void init() {}
}
