package dev.obscuria.elixirum.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.toolkit.Texture;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.fragmentum.util.color.ARGB;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.client.gui.GuiGraphics;

public interface GuiGraphicsUtil {

    static void setShaderColor(ARGB argb) {
        RenderSystem.setShaderColor(argb.red(), argb.green(), argb.blue(), argb.alpha());
    }

    static void setShaderColor(RGB rgb) {
        RenderSystem.setShaderColor(rgb.red(), rgb.green(), rgb.blue(), 1f);
    }

    static void resetShaderColor() {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    static void drawShifted(GuiGraphics graphics, Texture texture, HierarchicalControl node) {
        drawShifted(graphics, texture, node, 0, 0, 0, 0);
    }

    static void drawShifted(GuiGraphics graphics, Texture texture, HierarchicalControl node, int x, int y, int width, int height) {
        draw(graphics, texture, node.rect.x() + x, node.rect.y() + y, node.rect.width() + width, node.rect.height() + height);
    }

    static void draw(GuiGraphics graphics, Texture texture, int x, int y, int width, int height) {
        texture.render(graphics, x, y, width, height);
    }
}
