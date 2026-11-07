package dev.obscuria.elixirum.api.alchemy.components;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.components._CustomText;
import net.minecraft.network.chat.Component;

public interface CustomText {

    static CustomText of(Component text) {
        return new _CustomText(text);
    }

    static Codec<CustomText> codec() {
        return _CustomText.CODEC;
    }

    Component text();
}