package dev.obscuria.elixirum.client.screen.container;

import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class ListContainer extends HierarchicalWidget {
    private int topOffset;
    private int separation;
    private int bottomOffset;

    public ListContainer() {
        super(0, 0, 0, 0, Component.empty());
        this.setUpdateFlags(UPDATE_BY_WIDTH);
    }

    public ListContainer setOffset(int top, int bottom) {
        this.topOffset = top;
        this.bottomOffset = bottom;
        return this;
    }

    public ListContainer setSeparation(int value) {
        this.separation = value;
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        this.defaultRender(graphics, transform, mouseX, mouseY);
    }

    @Override
    protected void reorganize() {
        var offset = topOffset;
        for (var child : children()) {
            child.setX(getX());
            child.setY(getY() + offset);
            child.setWidth(getWidth());
            offset += child.getHeight() + separation;
        }
        this.setHeight(children().isEmpty() ? 0 : offset + bottomOffset - separation);
    }
}
