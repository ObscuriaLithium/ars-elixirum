package dev.obscuria.elixirum.client.screen.tool;

import net.minecraft.client.gui.components.AbstractWidget;

public record GlobalTransform(AbstractWidget widget, Rect scissor, Rect rect)
{
    public static GlobalTransform of(AbstractWidget widget, Rect scissor)
    {
        return new GlobalTransform(widget, scissor, Rect.of(widget));
    }

    public static GlobalTransform offset(AbstractWidget widget, Rect scissor, int x, int y)
    {
        return new GlobalTransform(widget, scissor, Rect.offset(widget, x, y));
    }

    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return isMouseOver((int) mouseX, (int) mouseY);
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return scissor.isMouseOver(mouseX, mouseY) && rect.isMouseOver(mouseX, mouseY);
    }

    public boolean isWithinScissor()
    {
        return rect.collides(scissor);
    }

    public GlobalTransform child(AbstractWidget child)
    {
        final var xDifference = rect.x() - widget.getX();
        final var yDifference = rect.y() - widget.getY();
        return new GlobalTransform(child, scissor, new Rect(
                child.getX() + xDifference,
                child.getY() + yDifference,
                child.getWidth(),
                child.getHeight()));
    }
}
