package dev.obscuria.elixirum.client.screen.tool;

import net.minecraft.client.gui.components.AbstractWidget;

public record Rect(int x, int y, int width, int height)
{
    public static Rect of(AbstractWidget widget)
    {
        return new Rect(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }

    public static Rect offset(AbstractWidget widget, int x, int y)
    {
        return new Rect(widget.getX() + x, widget.getY() + y, widget.getWidth(), widget.getHeight());
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= left() && mouseY >= top() && mouseX < right() && mouseY < bottom();
    }

    public boolean collides(Rect other)
    {
        return this.left() < other.right() &&
                this.right() > other.left() &&
                this.top() < other.bottom() &&
                this.bottom() > other.top();
    }

    public int left()
    {
        return x;
    }

    public int right()
    {
        return x + width;
    }

    public int top()
    {
        return y;
    }

    public int bottom()
    {
        return y + height;
    }
}
