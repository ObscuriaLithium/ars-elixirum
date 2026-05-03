package dev.obscuria.elixirum.client.screen.toolkit.controls.text;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.fragmentum.util.color.ARGB;
import net.minecraft.network.chat.Component;

public final class HeaderControl extends TextControl {

    public HeaderControl(Component content) {
        this(content, Palette.WHITE, true);
    }

    public HeaderControl(Component content, ARGB color) {
        this(content, color, true);
    }

    public HeaderControl(Component content, boolean centered) {
        this(content, Palette.WHITE, centered);
    }

    public HeaderControl(Component content, ARGB color, boolean centered) {
        setContent(GuiToolkit.dye(content, color));
        setScale(GuiToolkit.HEADER_SCALE);
        setCentered(centered);
    }

    @Override
    public void setContent(Component value) {
        super.setContent(GuiToolkit.applyFont(value));
    }
}
