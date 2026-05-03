package dev.obscuria.elixirum.client.screen.toolkit.controls.layout;

import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import net.minecraft.client.gui.GuiGraphics;

public final class HSpacingControl extends Control {

    private final int spacing;

    public HSpacingControl(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {}

    @Override
    protected void measure() {
        setMeasuredSize(0, spacing);
    }
}
