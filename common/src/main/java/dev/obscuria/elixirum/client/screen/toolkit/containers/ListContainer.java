package dev.obscuria.elixirum.client.screen.toolkit.containers;

import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import net.minecraft.client.gui.GuiGraphics;

public class ListContainer extends Control {

    private final int paddingTop;
    private final int paddingBottom;
    private final int paddingLeft;
    private final int paddingRight;
    private final int separation;

    public static Builder createBuilder() {
        return new Builder();
    }

    public ListContainer() {
        this(0, 0, 0, 0, 0);
    }

    public ListContainer(int paddingTop, int separation, int paddingBottom, int paddingLeft, int paddingRight) {
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.separation = separation;
        setSizeHints(SIZE_HINT_WIDTH);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        renderChildren(graphics, context, mouseX, mouseY);
    }

    @Override
    protected void measure() {
        int innerWidth = Math.max(0, getWidth() - paddingLeft - paddingRight);

        int totalHeight = paddingTop;
        boolean any = false;
        for (var child : getChildren()) {
            if (!child.isEffectivelyVisible()) continue;
            totalHeight += child.getMeasuredHeight() + separation;
            any = true;
        }
        if (any) totalHeight += paddingBottom - separation;
        else totalHeight = 0;
        setMeasuredSize(0, totalHeight);
    }

    @Override
    protected void layout() {
        int innerWidth = Math.max(0, getWidth() - paddingLeft - paddingRight);
        int offset = paddingTop;
        for (var child : getChildren()) {
            if (!child.visible) continue;
            placeChild(child, getX() + paddingLeft, getY() + offset, innerWidth, child.getMeasuredHeight());
            offset += child.getHeight() + separation;
        }
    }

    public static final class Builder {

        private int paddingTop = 0;
        private int paddingBottom = 0;
        private int paddingLeft = 0;
        private int paddingRight = 0;
        private int separation = 0;

        private Builder() {}

        public Builder paddingTop(int value) {
            this.paddingTop = value;
            return this;
        }

        public Builder paddingBottom(int value) {
            this.paddingBottom = value;
            return this;
        }

        public Builder paddingLeft(int value) {
            this.paddingLeft = value;
            return this;
        }

        public Builder paddingRight(int value) {
            this.paddingRight = value;
            return this;
        }

        public Builder separation(int value) {
            this.separation = value;
            return this;
        }

        public Builder paddingVertical(int value) {
            this.paddingTop = this.paddingBottom = value;
            return this;
        }

        public Builder paddingHorizontal(int value) {
            this.paddingLeft = this.paddingRight = value;
            return this;
        }

        public Builder padding(int value) {
            this.paddingTop = this.paddingBottom = this.paddingLeft = this.paddingRight = value;
            return this;
        }

        public ListContainer build() {
            return new ListContainer(paddingTop, separation, paddingBottom, paddingLeft, paddingRight);
        }
    }
}