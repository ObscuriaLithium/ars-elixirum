package dev.obscuria.elixirum.client.screen.toolkit.containers;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import net.minecraft.client.gui.GuiGraphics;

public class SplitContainer extends Control {

    private final float ratio;
    private final int separation;
    private final boolean drawSeparator;

    public SplitContainer(float ratio, int separation, boolean drawSeparator) {
        this.ratio = ratio;
        this.separation = separation;
        this.drawSeparator = drawSeparator;
        setSizeHints(SIZE_HINT_WIDTH | SIZE_HINT_HEIGHT);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext ctx, int mouseX, int mouseY) {

        if (drawSeparator) {
            int sx = getX() + (int) (getWidth() * ratio);
            graphics.vLine(
                    sx, getY() - 2, getY() + getHeight(),
                    Palette.MODERATE.decimal());
        }

        renderChildren(graphics, ctx, mouseX, mouseY);
    }

    @Override
    protected void measure() {
        var maxHeight = 0;
        for (var child : getChildren()) {
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
        }
        setMeasuredSize(0, maxHeight);
    }

    @Override
    protected void layout() {
        var children = getChildren();
        if (children.size() != 2) return;
        var left = children.get(0);
        var right = children.get(1);
        int split = (int) (getWidth() * ratio);

        placeChild(left, getX(), getY(), split - separation, getHeight());
        placeChild(right, getX() + split + separation + 1, getY(), getWidth() - split - separation - 1, getHeight());
    }
}
