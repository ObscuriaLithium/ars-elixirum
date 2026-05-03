package dev.obscuria.elixirum.client.screen.toolkit;

import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Control extends AbstractWidget {

    public static final int SIZE_HINT_NONE = 0;
    public static final int SIZE_HINT_WIDTH = 1;
    public static final int SIZE_HINT_HEIGHT = 1 << 1;

    private static final int DIRTY_MEASURE = 1;
    private static final int DIRTY_LAYOUT = 1 << 1;
    private static final int DIRTY_ALL = DIRTY_MEASURE | DIRTY_LAYOUT;
    private static final int MAX_PASSES = 4;

    private final List<Control> children = new ArrayList<>();
    private final List<Control> childrenView = Collections.unmodifiableList(children);

    private @Nullable Control parent;
    private int dirtyFlags = DIRTY_ALL;
    private int sizeHints = SIZE_HINT_NONE;
    private int measuredWidth = 0;
    private int measuredHeight = 0;

    public @Nullable SoundEvent clickSound = ElixirumSounds.UI_CLICK_1;
    public @Nullable ClickHandler<? super Control> clickHandler;

    protected Control(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    protected Control() {
        this(0, 0, 0, 0, CommonComponents.EMPTY);
    }

    public abstract void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY);

    protected void measure() {
        setMeasuredSize(width, height);
    }

    protected void layout() {}

    protected final void setMeasuredSize(int width, int height) {
        this.measuredWidth = Math.max(0, width);
        this.measuredHeight = Math.max(0, height);
    }

    public final float getCenterX() {
        return getX() + getWidth() * 0.5f;
    }

    public final float getCenterY() {
        return getY() + getHeight() * 0.5f;
    }

    public final int getMeasuredWidth() {
        return measuredWidth;
    }

    public final int getMeasuredHeight() {
        return measuredHeight;
    }

    public final void setRequiredHeight(int height) {
        setMeasuredSize(getMeasuredWidth(), height);
        if (this.height == height) return;
        this.height = height;
        this.markDirty();
    }

    protected final void placeChild(
            Control child,
            int x, int y, int width, int height
    ) {
        int w = child.measuredWidth == 0 ? width : Math.max(width, child.measuredWidth);
        int h = child.measuredHeight > 0 ? child.measuredHeight : height;

        int dx = x - child.getX();
        int dy = y - child.getY();
        if (dx != 0 || dy != 0) {
            child.setX(x);
            child.setY(y);
            child.translateSubtree(dx, dy);
        }

        boolean wChanged = w != child.width;
        boolean hChanged = h != child.height;
        if (wChanged || hChanged) {
            child.width = w;
            child.height = h;
            boolean needsRemeasure = (wChanged && (child.sizeHints & SIZE_HINT_WIDTH) != 0)
                    || (hChanged && (child.sizeHints & SIZE_HINT_HEIGHT) != 0);
            if (needsRemeasure) {
                child.dirtyFlags |= DIRTY_ALL;
                bubbleFromChild();
            }
        }
    }

    public final void setPosition(int x, int y) {
        int dx = x - getX();
        int dy = y - getY();
        if (dx == 0 && dy == 0) return;
        setX(x);
        setY(y);
        translateSubtree(dx, dy);
    }

    public final void setAllocatedSize(int width, int height) {
        boolean wChanged = width != this.width;
        boolean hChanged = height != this.height;
        if (!wChanged && !hChanged) return;
        this.width = width;
        this.height = height;
        markDirty();
    }

    private void translateSubtree(int dx, int dy) {
        for (var child : children) {
            child.setX(child.getX() + dx);
            child.setY(child.getY() + dy);
            child.translateSubtree(dx, dy);
        }
    }

    public final void markDirty() {
        this.dirtyFlags |= DIRTY_ALL;
        if (parent != null) parent.bubbleFromChild();
    }

    final void bubbleFromChild() {
        if ((dirtyFlags & DIRTY_ALL) == DIRTY_ALL) return;
        dirtyFlags |= DIRTY_ALL;
        if (parent != null) parent.bubbleFromChild();
    }

    protected final void setSizeHints(int hints) {
        this.sizeHints = hints;
    }

    public final void performLayoutIfNeeded() {
        for (int pass = 0; pass < MAX_PASSES && (dirtyFlags & DIRTY_ALL) != 0; pass++) {
            measurePass();
            layoutPass();
        }
    }

    private void measurePass() {
        boolean anyChildRemeasured = false;
        for (var child : children) {
            if ((child.dirtyFlags & DIRTY_MEASURE) != 0) {
                child.measurePass();
                anyChildRemeasured = true;
            }
        }
        if (anyChildRemeasured) {
            dirtyFlags |= DIRTY_MEASURE;
        }
        if ((dirtyFlags & DIRTY_MEASURE) != 0) {
            measure();
            dirtyFlags &= ~DIRTY_MEASURE;
        }
    }

    private void layoutPass() {
        if ((dirtyFlags & DIRTY_LAYOUT) != 0) {
            layout();
            dirtyFlags &= ~DIRTY_LAYOUT;
        }
        for (var child : children) {
            if ((child.dirtyFlags & DIRTY_LAYOUT) != 0) child.layoutPass();
        }
    }

    private void renderPass(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (!isEffectivelyVisible()) return;
        var hovered = context.isMouseOver(this, mouseX, mouseY);
        if (isHovered != hovered) {
            this.isHovered = hovered;
            if (hovered && isFocusable()) {
                playSound(ElixirumSounds.UI_HOVER, 0.15f);
            }
        }
        render(graphics, context, mouseX, mouseY);
    }

    public final <T extends Control> T add(T node) {
        addChild(node);
        return node;
    }

    public final void addChild(Control node) {
        if (node.parent != null) node.parent.removeChild(node);
        children.add(node);
        node.parent = this;
        markDirty();
    }

    public final void removeChild(Control node) {
        if (children.remove(node)) {
            node.parent = null;
            markDirty();
        }
    }

    public final void clearChildren() {
        if (children.isEmpty()) return;
        for (var child : children) child.parent = null;
        children.clear();
        markDirty();
    }

    public final List<Control> getChildren() {
        return childrenView;
    }

    public final boolean hasChildren() {
        return !children.isEmpty();
    }

    public final @Nullable Control getParent() {
        return parent;
    }

    public final void setVisible(boolean visible) {
        if (this.visible == visible) return;
        this.visible = visible;
        markDirty();
    }

    public final boolean isEffectivelyVisible() {
        if (!visible) return false;
        return parent == null || parent.isEffectivelyVisible();
    }

    public boolean mouseClicked(GuiContext context, double mouseX, double mouseY, int button) {
        if (!isEffectivelyVisible() || !active) return false;
        if (clickHandler != null
                && clickHandler.canConsume(button)
                && context.isMouseOver(this, mouseX, mouseY)
                && clickHandler.invoke(this)) {
            playClickSound();
            return true;
        }
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseClicked(context, mouseX, mouseY, button)) return true;
        }
        return false;
    }

    public boolean mouseScrolled(GuiContext ctx, double mouseX, double mouseY, double delta) {
        if (!isEffectivelyVisible()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseScrolled(ctx, mouseX, mouseY, delta)) return true;
        }
        return false;
    }

    public boolean mouseDragged(GuiContext context, double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!isEffectivelyVisible()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseDragged(context, mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return false;
    }

    public boolean mouseReleased(GuiContext context, double mouseX, double mouseY, int button) {
        if (!isEffectivelyVisible()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).mouseReleased(context, mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isEffectivelyVisible()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!isEffectivelyVisible()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).keyReleased(keyCode, scanCode, modifiers)) return true;
        }
        return true;
    }

    public boolean isFocusable() {
        return false;
    }

    private Control getRoot() {
        Control node = this;
        while (node.parent != null) node = node.parent;
        return node;
    }

    private static void clearFocusInSubtree(Control node, Control except) {
        if (node != except && node.isFocused()) {
            node.setFocused(false);
        }
        for (var child : node.children) {
            clearFocusInSubtree(child, except);
        }
    }

    @Override
    public void setFocused(boolean focused) {
        if (focused && isFocusable()) {
            clearFocusInSubtree(getRoot(), this);
        }
        super.setFocused(focused);
    }

    public void tick() {
        for (var child : children) child.tick();
    }

    protected final void playClickSound() {
        if (clickSound == null) return;
        playSound(clickSound);
    }

    protected final void playSound(SoundEvent sound) {
        playSound(sound, 1.0F);
    }

    protected final void playSound(SoundEvent sound, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F, volume));
    }

    @Override
    protected final void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        performLayoutIfNeeded();
        var context = GuiContext.forRoot(this);
        renderPass(graphics, context, mouseX, mouseY);
        context.release();
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        var context = GuiContext.forRoot(this);
        boolean result = mouseClicked(context, mouseX, mouseY, button);
        context.release();
        return result;
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        var context = GuiContext.forRoot(this);
        boolean result = mouseScrolled(context, mouseX, mouseY, delta);
        context.release();
        return result;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var context = GuiContext.forRoot(this);
        boolean result = mouseDragged(context, mouseX, mouseY, button, dragX, dragY);
        context.release();
        return result;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var context = GuiContext.forRoot(this);
        boolean result = mouseReleased(context, mouseX, mouseY, button);
        context.release();
        return result;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!isEffectivelyVisible()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).charTyped(codePoint, modifiers)) return true;
        }
        return false;
    }

    @Override
    protected final void updateWidgetNarration(NarrationElementOutput out) {
        if (isFocused() && !getMessage().getString().isEmpty())
            out.add(NarratedElementType.TITLE, getMessage());
        for (var child : children) child.updateWidgetNarration(out);
    }

    protected final void renderChildren(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        for (var child : children)
            child.renderPass(graphics, context, mouseX, mouseY);
    }

    protected boolean hasVisibleContent() {
        if (isEffectivelyVisible() && hasOwnContent()) return true;
        for (var child : children) if (child.hasVisibleContent()) return true;
        return false;
    }

    protected boolean hasOwnContent() {
        return false;
    }

    @FunctionalInterface
    public interface ClickHandler<T extends Control> {

        boolean invoke(T node);

        default boolean canConsume(int button) {
            return button == 0;
        }

        static <T extends Control> ClickHandler<Control> leftClick(
                Class<T> type, ClickHandler<T> handler
        ) {
            return node -> type.isInstance(node) && handler.invoke(type.cast(node));
        }

        static <T extends Control> ClickHandler<Control> rightClick(
                Class<T> type, ClickHandler<T> handler
        ) {
            return new ClickHandler<>() {

                @Override
                public boolean invoke(Control node) {
                    return type.isInstance(node) && handler.invoke(type.cast(node));
                }

                @Override
                public boolean canConsume(int button) {
                    return button == 1;
                }
            };
        }

        static ClickHandler<Control> onClick(ClickHandler<Control> handler) {
            return handler;
        }
    }
}