package dev.obscuria.elixirum.client.screen.toolkit.controls.text;

import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Label;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public abstract class TextControl extends Control {

    @Getter private Component content = CommonComponents.EMPTY;
    @Getter private float scale = 1f;
    @Getter private boolean centered = false;

    private Label label = Label.EMPTY;

    protected TextControl() {
        setSizeHints(SIZE_HINT_WIDTH);
    }

    public void setContent(Component value) {
        this.content = value;
        markDirty();
    }

    public void setScale(float value) {
        this.scale = GuiToolkit.snapScale(value);
        markDirty();
    }

    public void setCentered(boolean value) {
        this.centered = value;
        markDirty();
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (label.isEmpty() || !context.isVisible(this)) return;
        if (centered) this.label.drawHCentered(graphics, getCenterX(), getY(), 0xffffffff);
        else this.label.draw(graphics, getX(), getY(), 0xffffffff);
    }

    @Override
    protected void measure() {
        this.label = Label.create(content, getWidth() - 3, scale);
        setRequiredHeight(label.getHeight());
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }
}