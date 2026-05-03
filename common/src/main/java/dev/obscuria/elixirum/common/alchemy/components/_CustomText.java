package dev.obscuria.elixirum.common.alchemy.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.components.CustomText;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

public record _CustomText(Component text) implements CustomText {

    public static final Codec<CustomText> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                ExtraCodecs.COMPONENT.fieldOf("text").forGetter(CustomText::text)
        ).apply(codec, CustomText::of));
    }
}
