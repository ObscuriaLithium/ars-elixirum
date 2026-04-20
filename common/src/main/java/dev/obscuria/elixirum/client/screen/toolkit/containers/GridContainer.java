package dev.obscuria.elixirum.client.screen.toolkit.containers;

import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class GridContainer extends HierarchicalControl {

    private final int separation;

    private final int marginLeft;
    private final int marginRight;
    private final int marginTop;
    private final int marginBottom;

    public GridContainer(int separation) {
        this(separation, 0, 0, 0, 0);
    }

    public GridContainer(int separation, int marginLeft, int marginRight, int marginTop, int marginBottom) {
        super(0, 0, 0, 0, Component.empty());
        this.separation = separation;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.setUpdateFlags(UPDATE_BY_WIDTH);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        this.renderChildren(graphics, transform, mouseX, mouseY);
    }

    @Override
    public void reorganize() {

        var offsetX = marginLeft;
        var offsetY = marginTop;
        var maxHeight = 0;

        for (var child : listChildren().toList()) {
            final var width = offsetX + separation + child.rect.width();

            if (width > rect.width()) {
                offsetX = marginLeft;
                offsetY += maxHeight + separation;
                maxHeight = 0;
            } else if (rect.width() < width + marginRight) {
                rect.setWidth(width + marginRight - 1);
            }

            child.rect.setX(rect.x() + offsetX);
            child.rect.setY(rect.y() + offsetY);

            offsetX += separation + child.rect.width();
            maxHeight = Math.max(maxHeight, child.rect.height());
        }

        rect.setHeight(hasChildren()
                ? offsetY + maxHeight + marginBottom
                : 0);
    }
}