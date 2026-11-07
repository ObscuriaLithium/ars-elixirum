package dev.obscuria.elixirum.client.screen.toolkit.containers;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SpoilerContainer extends Control {

    private static final int HEADER_H = 14;

    private boolean expanded = false;

    public SpoilerContainer(Component name) {
        super(0, 0, 0, HEADER_H, name);
    }

    @Override
    protected void measure() {
        int bodyMeasured = 0;
        for (var child : getChildren()) {
            bodyMeasured = Math.max(bodyMeasured, child.getMeasuredHeight());
        }
        int desired = HEADER_H + (expanded ? bodyMeasured + 4 : 0);
        setMeasuredSize(0, desired);
    }

    @Override
    protected void layout() {
        if (!expanded) return;
        for (var child : getChildren())
            placeChild(child, getX() + 2, getY() + HEADER_H + 2,
                    getWidth() - 4, child.getMeasuredHeight());
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public boolean mouseClicked(GuiContext context, double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        if (context.isMouseOver(this, mouseX, mouseY) && mouseY < getY() + HEADER_H) {
            expanded = !expanded;
            markDirty();
            return true;
        }
        return expanded && super.mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFocused() && (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
                || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE)) {
            expanded = !expanded;
            markDirty();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        boolean hovered = context.isMouseOver(this, mouseX, mouseY) && mouseY < getY() + HEADER_H;

        RenderSystem.enableBlend();
        context.pushAlpha(hovered ? 0.6f : 0.3f);
        GuiToolkit.draw(graphics, Textures.SOLID_LIGHT, getX(), getY(), getWidth(), HEADER_H);
        context.popModulate();

        if (expanded) {
            context.pushAlpha(0.1f);
            GuiToolkit.draw(graphics, Textures.SOLID_LIGHT, this);
            context.popModulate();
        }
        RenderSystem.disableBlend();

        var font = Minecraft.getInstance().font;
        graphics.drawString(font, getMessage(), getX() + 3, getY() + 3, -1);
        GuiToolkit.draw(graphics,
                expanded ? Textures.ARROW_UP : Textures.ARROW_DOWN,
                getX() + getWidth() - 10, getY() + 3, 8, 8);

        if (expanded) renderChildren(graphics, context, mouseX, mouseY);
    }
}