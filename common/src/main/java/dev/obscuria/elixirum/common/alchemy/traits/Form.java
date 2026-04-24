package dev.obscuria.elixirum.common.alchemy.traits;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.helpers.CodecHelper;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import dev.obscuria.elixirum.helpers.IdRepresentable;
import dev.obscuria.elixirum.common.alchemy.ElixirQuality;
import dev.obscuria.elixirum.common.alchemy.traits.application.ApplicationBehavior;
import dev.obscuria.elixirum.common.alchemy.traits.application.ThrowableBehavior;
import dev.obscuria.elixirum.common.alchemy.traits.application.PotableBehavior;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Locale;

public enum Form implements Trait, ApplicationBehavior, IdRepresentable, StringRepresentable {
    POTABLE(0, Icon.APPLICATION_POTABLE, new PotableBehavior()),
    EXPLOSIVE(1, Icon.APPLICATION_EXPLOSIVE, new ThrowableBehavior()),
    LINGERING(2, Icon.APPLICATION_LINGERING, new ThrowableBehavior());
    //CONTAGIOUS
    //AURA

    public static final Codec<Form> CODEC;
    public static final Codec<Form> ID_CODEC;
    public static final Codec<Form> STRING_CODEC;
    public static final Component NAME;
    public static final RGB COLOR;

    @Getter private final Component displayName;
    @Getter private final Component description;
    @Getter private final ApplicationBehavior behavior;
    @Getter private final Icon icon;
    @Getter private final int id;

    Form(int id, Icon icon, ApplicationBehavior behavior) {
        this.displayName = Component.translatable("trait.elixirum.form." + getSerializedName());
        this.description = Component.translatable("trait.elixirum.form." + getSerializedName() + ".desc");
        this.behavior = behavior;
        this.icon = icon;
        this.id = id;
    }

    public Component makeElixirName(ItemStack stack) {
        var contents = ContentsHelper.elixir(stack);
        var quality = ElixirQuality.fromContents(contents);
        var isMixture = quality == ElixirQuality.MIXTURE;
        var isPotable = this == POTABLE;
        if (isMixture) {
            return isPotable
                    ? Component.translatable("item.elixirum.elixir.name_simple_mixture")
                    : Component.translatable("item.elixirum.elixir.name_complex_mixture",
                    getDisplayName());
        } else {
            return isPotable
                    ? Component.translatable("item.elixirum.elixir.name_simple",
                    contents.displayName(), quality.getDisplayName())
                    : Component.translatable("item.elixirum.elixir.name_complex",
                    getDisplayName(), contents.displayName(), quality.getDisplayName());
        }
    }

    @Override
    public Component getCategoryName() {
        return NAME;
    }

    @Override
    public RGB getColor() {
        return COLOR;
    }

    @Override
    public UseAnim getUseAnim(ItemStack stack) {
        return behavior.getUseAnim(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return behavior.getUseDuration(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, Level level, LivingEntity entity) {
        return behavior.finishUsing(stack, level, entity);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        this.behavior.releaseUsing(stack, level, entity, timeLeft);
    }

    @Override
    public float getProgressShift() {
        return -10;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    static {
        ID_CODEC = IdRepresentable.fromEnum(Form.values(), POTABLE);
        STRING_CODEC = StringRepresentable.fromEnum(Form::values);
        CODEC = CodecHelper.alternative(ID_CODEC, STRING_CODEC);
        NAME = Component.translatable("trait.elixirum.form");
        COLOR = Colors.rgbOf(0xaf7ac8);
    }
}
