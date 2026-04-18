package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.ArsElixirum;
import net.minecraft.network.chat.Component;

public class HeaderControl extends TextControl {

    public HeaderControl(Component content) {
        this.setContent(content);
        this.setCentered(true);
        this.setScale(1.2f);
    }

    @Override
    public void setContent(Component value) {
        super.setContent(value.copy().withStyle(style -> style.withFont(ArsElixirum.FONT)));
    }
}
