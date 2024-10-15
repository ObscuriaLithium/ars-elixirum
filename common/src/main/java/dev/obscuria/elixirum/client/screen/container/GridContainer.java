package dev.obscuria.elixirum.client.screen.container;

import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GridContainer extends HierarchicalWidget
{
    private int separation;

    public GridContainer()
    {
        super(0, 0, 0, 0, Component.empty());
        this.setUpdateFlags(UPDATE_BY_WIDTH);
    }

    public GridContainer setSeparation(int value)
    {
        this.separation = value;
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        this.defaultRender(graphics, transform, mouseX, mouseY);
    }

    @Override
    protected void reorganize()
    {
        var xOffset = 0;
        var yOffset = 0;
        var maxHeight = 0;
        for (var child : children())
        {
            if (xOffset + separation + child.getWidth() > getWidth())
            {
                xOffset = 0;
                yOffset += maxHeight + separation;
                maxHeight = 0;
            }
            child.setX(getX() + xOffset);
            child.setY(getY() + yOffset);
            xOffset += separation + child.getWidth();
            maxHeight = Math.max(maxHeight, child.getHeight());
        }
        this.setHeight(children().isEmpty() ? 0 : yOffset + maxHeight);
    }
}
