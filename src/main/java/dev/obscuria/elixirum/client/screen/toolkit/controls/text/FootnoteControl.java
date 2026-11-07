package dev.obscuria.elixirum.client.screen.toolkit.controls.text;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.fragmentum.util.color.ARGB;
import net.minecraft.network.chat.Component;

public final class FootnoteControl extends TextControl {

    public FootnoteControl(Component content) {
        this(content, Palette.LIGHT, true);
    }

    public FootnoteControl(Component content, ARGB color) {
        this(content, color, true);
    }

    public FootnoteControl(Component content, boolean centered) {
        this(content, Palette.LIGHT, centered);
    }

    public FootnoteControl(Component content, ARGB color, boolean centered) {
        setContent(GuiToolkit.dye(content, color));
        setScale(GuiToolkit.FOOTNOTE_SCALE);
        setCentered(centered);
    }
}
