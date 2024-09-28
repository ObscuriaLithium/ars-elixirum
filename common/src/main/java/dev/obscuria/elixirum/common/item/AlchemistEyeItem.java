package dev.obscuria.elixirum.common.item;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.EssenceBlacklist;
import dev.obscuria.elixirum.registry.ElixirumAttributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

@EssenceBlacklist
public final class AlchemistEyeItem extends ArmorItem {

    public AlchemistEyeItem() {
        super(ArmorMaterials.IRON, Type.HELMET, new Properties()
                .rarity(Rarity.UNCOMMON)
                .durability(512)
                .attributes(defaultAttributeModifiers()));
    }

    private static ItemAttributeModifiers defaultAttributeModifiers() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.BLOCK_INTERACTION_RANGE,
                        new AttributeModifier(
                                Elixirum.key("default"), 2,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD)
                .add(ElixirumAttributes.POTION_MASTERY.holder(),
                        new AttributeModifier(
                                Elixirum.key("default"), 10,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD)
                .add(ElixirumAttributes.POTION_IMMUNITY.holder(),
                        new AttributeModifier(
                                Elixirum.key("default"), 10,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD)
                .build();
    }
}
