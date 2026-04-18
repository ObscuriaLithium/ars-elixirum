package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.ArsElixirum;
import net.minecraft.network.chat.Component;

public class SubHeaderControl extends TextControl {

    public SubHeaderControl(Component content) {
        this.setContent(content);
        this.setCentered(true);
    }

    @Override
    public void setContent(Component value) {
        super.setContent(value.copy().withStyle(style -> style.withFont(ArsElixirum.FONT)));
    }
}
