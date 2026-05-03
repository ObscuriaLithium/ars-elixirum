package dev.obscuria.elixirum.client.screen.toolkit.containers;

import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import net.minecraft.client.gui.GuiGraphics;

public class GridContainer extends Control {

    private final int separation;
    private final int marginLeft, marginRight, marginTop, marginBottom;

    public GridContainer(int separation) {
        this(separation, 0, 0, 0, 0);
    }

    public GridContainer(
            int separation,
            int marginLeft, int marginRight,
            int marginTop, int marginBottom
    ) {
        this.separation = separation;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        setSizeHints(SIZE_HINT_WIDTH);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        renderChildren(graphics, context, mouseX, mouseY);
    }

    @Override
    protected void measure() {
        int offsetX = marginLeft;
        int offsetY = 0;
        int maxRowH = 0;

        for (var child : getChildren()) {
            int cw = child.getMeasuredWidth();
            int ch = child.getMeasuredHeight();
            if (offsetX + cw > getWidth() - marginRight && offsetX > marginLeft) {
                offsetX = marginLeft;
                offsetY += maxRowH + separation;
                maxRowH = 0;
            }
            offsetX += cw + separation;
            maxRowH = Math.max(maxRowH, ch);
        }

        setRequiredHeight(marginTop + offsetY + maxRowH + marginBottom);
    }

    @Override
    protected void layout() {
        int offsetX = marginLeft;
        int offsetY = marginTop;
        int maxRowH = 0;

        for (var child : getChildren()) {
            int cw = child.getMeasuredWidth();
            int ch = child.getMeasuredHeight();
            if (offsetX + cw > getWidth() - marginRight && offsetX > marginLeft) {
                offsetX = marginLeft;
                offsetY += maxRowH + separation;
                maxRowH = 0;
            }
            placeChild(child, getX() + offsetX, getY() + offsetY, cw, ch);
            offsetX += cw + separation;
            maxRowH = Math.max(maxRowH, ch);
        }
    }
}
