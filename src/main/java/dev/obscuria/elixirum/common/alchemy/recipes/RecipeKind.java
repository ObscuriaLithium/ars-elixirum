package dev.obscuria.elixirum.common.alchemy.recipes;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum RecipeKind implements StringRepresentable {
    COMMON(ChatFormatting.WHITE),
    ETERNAL(ChatFormatting.GOLD),
    FORBIDDEN(ChatFormatting.LIGHT_PURPLE);

    public static final Codec<RecipeKind> CODEC = StringRepresentable.fromEnum(RecipeKind::values);

    public final ChatFormatting color;
    public final Component displayName;
    public final Component description;

    RecipeKind(ChatFormatting color) {
        this.color = color;
        this.displayName = Component.translatable("recipe_kind.elixirum.%s".formatted(this.getSerializedName()));
        this.description = Component.translatable("recipe_kind.elixirum.%s.desc".formatted(this.getSerializedName()));
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
