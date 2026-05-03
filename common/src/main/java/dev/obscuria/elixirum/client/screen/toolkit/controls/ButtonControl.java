package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ButtonControl extends Control {

    public ButtonControl(Component name) {
        super(0, 0, 14, 14, name);
        this.clickHandler = ClickHandler.leftClick(ButtonControl.class, self -> {
            self.onClick();
            return true;
        });
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (!context.isWithinScissor(this)) return;
        var font = Minecraft.getInstance().font;
        boolean hovered = active && context.isMouseOver(this, mouseX, mouseY);

        if (!active) context.pushAlpha(0.33f);
        GuiToolkit.draw(graphics, pickTexture(hovered), this);
        graphics.drawCenteredString(font, pickButtonName(), getX() + getWidth() / 2, getY() + 3, -1);
        if (!active) context.popModulate();
    }

    @Override
    protected void measure() {
        setMeasuredSize(14, 14);
    }

    protected Texture pickTexture(boolean hovered) {
        return Textures.buttonGray(hovered);
    }

    protected Component pickButtonName() {
        return getMessage();
    }

    protected void onClick() {}
}