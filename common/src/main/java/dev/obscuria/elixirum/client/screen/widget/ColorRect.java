package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class ColorRect extends HierarchicalWidget
{
    private final int color;

    public ColorRect(int x, int y, int width, int height, int color)
    {
        super(x, y, width, height, Component.empty());
        this.color = color;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        if (!transform.isWithinScissor()) return;
        this.isHovered = transform.isMouseOver(mouseX, mouseY);
        graphics.fill(getX(), getY(), getRight(), getBottom(), isHovered ? 0xFFFF00FF : color);
        this.defaultRender(graphics, transform, mouseX, mouseY);
    }

    @Override
    protected void reorganize()
    {

    }
}
