package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.ArsElixirumPalette;
import net.minecraft.network.chat.Component;

public class ParagraphControl extends TextControl {

    public ParagraphControl(Component content) {
        this(content, false);
    }

    public ParagraphControl(Component content, boolean centered) {
        this.setContent(content.copy().withStyle(style -> style.withColor(ArsElixirumPalette.LIGHT.decimal())));
        this.setCentered(centered);
        this.setScale(0.66f);
    }
}