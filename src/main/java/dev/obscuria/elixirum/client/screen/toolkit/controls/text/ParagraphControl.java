package dev.obscuria.elixirum.client.screen.toolkit.controls.text;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.fragmentum.util.color.ARGB;
import net.minecraft.network.chat.Component;

public final class ParagraphControl extends TextControl {

    public ParagraphControl(Component content) {
        this(content, Palette.LIGHT, false);
    }

    public ParagraphControl(Component content, ARGB color) {
        this(content, color, false);
    }

    public ParagraphControl(Component content, boolean centered) {
        this(content, Palette.LIGHT, centered);
    }

    public ParagraphControl(Component content, ARGB color, boolean centered) {
        setContent(GuiToolkit.dye(content, color));
        setScale(GuiToolkit.PARAGRAPH_SCALE);
        setCentered(centered);
    }
}
