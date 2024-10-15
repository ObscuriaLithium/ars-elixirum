package dev.obscuria.elixirum.client.screen.container;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class SplitContainer extends HierarchicalWidget
{
    private final double ratio;

    public SplitContainer(double ratio)
    {
        super(0, 0, 0, 0, Component.empty());
        this.setUpdateFlags(UPDATE_BY_WIDTH);
        this.ratio = ratio;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        ElixirumScreen.debugRenderer(this, graphics, transform, mouseX, mouseY);
        this.defaultRender(graphics, transform, mouseX, mouseY);
        graphics.vLine(getX() + (int) (getWidth() * ratio), getY() - 1, getBottom(), 0xFF80788A);
    }

    @Override
    protected void reorganize()
    {
        if (children().size() != 2)
            throw new IllegalStateException("Split container must have 2 child widgets");
        final var first = children().getFirst();
        final var second = children().getLast();
        final var split = (int) (getWidth() * ratio);
        first.setX(getX());
        first.setY(getY());
        first.setWidth(split - 5);
        first.setHeight(getHeight());
        second.setX(getX() + split + 6);
        second.setY(getY());
        second.setWidth(getWidth() - split - 6);
        second.setHeight(getHeight());
    }
}
