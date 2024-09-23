package dev.obscuria.elixirum.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.function.Function;

public interface TextWrapper {

    static TextWrapper create(String text) {
        return TextWrapperImpl.create(text);
    }

    static TextWrapper create(Component component) {
        return TextWrapperImpl.create(component);
    }

    TextWrapper setMaxLength(int length);

    TextWrapper setPrefix(String prefix);

    TextWrapper setLinePrefix(int index, String prefix);

    TextWrapper setStyle(Style style);

    TextWrapper setLineStyle(int index, Style style);

    TextWrapper fragment(Function<String, Component> function);

    List<? extends Component> build();
}
