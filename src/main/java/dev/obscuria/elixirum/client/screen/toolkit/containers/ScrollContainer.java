package dev.obscuria.elixirum.client.screen.toolkit.containers;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Label;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ScrollContainer extends Control {

    private Label placeholder = Label.EMPTY;

    private double scrollTarget = 0.0;
    private double scrollSmooth = 0.0;
    private double scrollSmoothO = 0.0;
    private int contentHeight = 0;
    private int tickCount = 0;
    private int lastSoundTick = -999;

    private boolean isDraggingScrollbar = false;
    private double dragStartY = 0.0;
    private double dragStartScroll = 0.0;

    public ScrollContainer(Component placeholder) {
        super(0, 0, 0, 0, placeholder);
        setSizeHints(SIZE_HINT_HEIGHT);
    }

    public void resetScroll() {
        this.scrollTarget = scrollSmooth = scrollSmoothO = 0;
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        double scroll = lerpScroll();

        context.pushScissor(graphics, getX(), getY(), getX() + getWidth(), getY() + getHeight());
        graphics.pose().pushPose();
        graphics.pose().translate(0.0, -scroll, 0.0);
        context.pushScrollOffset(scroll);
        renderChildren(graphics, context, mouseX, mouseY);
        context.popScrollOffset();
        graphics.pose().popPose();
        context.popScissor(graphics);

        renderScrollbar(graphics, scroll);
        renderFades(graphics, scroll);

        if (!hasVisibleContent()) this.placeholder.drawHVCentered(graphics,
                getCenterX(), getCenterY(),
                Palette.LIGHT.decimal());
    }

    @Override
    public boolean mouseClicked(GuiContext context, double mouseX, double mouseY, int button) {
        if (button == 0 && scrollEnabled() && isOverScrollbar(mouseX, mouseY)) {
            double scroll = lerpScroll();
            double clamped = Mth.clamp(scroll, 0.0, maxScroll());
            float ratio = (float) (clamped / maxScroll());
            int barH = Math.max(8, (int) (getHeight() * ((double) getHeight() / contentHeight)));
            int barOff = (int) ((getHeight() - barH) * ratio);
            int barTop = getY() + barOff;
            int barBottom = barTop + barH;

            if (mouseY >= barTop && mouseY <= barBottom) {
                isDraggingScrollbar = true;
                dragStartY = mouseY - barTop;
                dragStartScroll = scrollTarget;
            } else {
                double trackSize = getHeight() - barH;
                double desiredBarTop = mouseY - getY() - barH / 2.0;
                scrollTarget = Mth.clamp(desiredBarTop / trackSize * maxScroll(), 0.0, maxScroll());
            }
            return true;
        }

        if (!context.isMouseOver(this, mouseX, mouseY)) return false;
        context.pushScrollOffset(lerpScroll());
        var result = super.mouseClicked(context, mouseX, mouseY, button);
        context.popScrollOffset();
        return result;
    }

    @Override
    public boolean mouseDragged(GuiContext context, double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScrollbar && button == 0 && scrollEnabled()) {
            int barH = Math.max(8, (int) (getHeight() * ((double) getHeight() / contentHeight)));
            double trackSize = getHeight() - barH;
            if (trackSize > 0) {
                double desiredBarTop = mouseY - getY() - dragStartY;
                scrollTarget = Mth.clamp(desiredBarTop / trackSize * maxScroll(), 0.0, maxScroll());
            }
            return true;
        }

        context.pushScrollOffset(lerpScroll());
        var result = super.mouseDragged(context, mouseX, mouseY, button, dragX, dragY);
        context.popScrollOffset();
        return result;
    }

    @Override
    public boolean mouseReleased(GuiContext context, double mouseX, double mouseY, int button) {
        if (isDraggingScrollbar && button == 0) {
            isDraggingScrollbar = false;
            return true;
        }

        context.pushScrollOffset(lerpScroll());
        var result = super.mouseReleased(context, mouseX, mouseY, button);
        context.popScrollOffset();
        return result;
    }

    @Override
    public boolean mouseScrolled(GuiContext context, double mouseX, double mouseY, double delta) {
        if (!context.isMouseOver(this, mouseX, mouseY) || !scrollEnabled()) return false;
        this.scrollTarget += delta * -16.0;
        return true;
    }

    @Override
    public void tick() {
        tickCount++;
        scrollSmoothO = scrollSmooth;
        scrollSmooth = Mth.lerp(0.5, scrollSmooth, scrollTarget);

        double scrollDelta = Math.abs(scrollSmooth - scrollSmoothO);
        if (scrollDelta > 0.25) {
            float t = (float) Mth.clamp((scrollDelta - 0.5) / (3.0 - 0.5), 0.0, 1.0);
            int cooldown = Math.round(Mth.lerp(t, 10, 1));
            if (tickCount - lastSoundTick >= cooldown) {
                double dir = scrollSmooth - scrollSmoothO;
                var soundInstance = SimpleSoundInstance.forUI(ElixirumSounds.UI_SCROLL, dir < 0 ? 0.9f : 1.1f);
                Minecraft.getInstance().getSoundManager().play(soundInstance);
                this.lastSoundTick = tickCount;
            }
        }
        if (!isDraggingScrollbar) {
            if (!scrollEnabled() || scrollTarget < 0)
                scrollTarget = Mth.lerp(0.75, scrollTarget, 0.0);
            else if (scrollTarget > maxScroll())
                scrollTarget = Mth.lerp(0.75, scrollTarget, maxScroll());
        }
        super.tick();
    }

    @Override
    protected void measure() {
        this.placeholder = Label.create(getMessage(), getWidth() - 10, GuiToolkit.PARAGRAPH_SCALE);
        setMeasuredSize(0, 0);
    }

    @Override
    protected void layout() {
        this.contentHeight = 0;
        int offsetY = getY() + 5;
        for (var child : getChildren()) {
            placeChild(child, getX(), offsetY, getWidth() - 2, child.getMeasuredHeight());
            offsetY += child.getHeight() + 5;
            this.contentHeight += child.getHeight() + 10;
        }
    }

    private void renderScrollbar(GuiGraphics graphics, double scroll) {
        if (!scrollEnabled()) return;
        double clamped = Mth.clamp(scroll, 0.0, maxScroll());
        float ratio = (float) (clamped / maxScroll());
        int barH = Math.max(8, (int) (getHeight() * ((double) getHeight() / contentHeight)));
        int barOff = (int) ((getHeight() - barH) * ratio);
        int bx = getX() + getWidth() - 1;

        graphics.fill(bx, getY(), bx + 2, getY() + getHeight(), 0x2F000000);
        graphics.pose().pushPose();
        graphics.pose().translate(bx, getY() + barOff, 0);
        graphics.fill(0, 0, 2, barH, Palette.ACCENT.withAlpha(100 / 255f).decimal());
        graphics.pose().popPose();
    }

    private void renderFades(GuiGraphics graphics, double scroll) {
        if (scroll > 5)
            graphics.fill(getX(), getY(), getX() + getWidth() - 1, getY() + 5, 0x6080788A);
        if (contentHeight - scroll > getHeight() + 5)
            graphics.fill(getX(), getY() + getHeight() - 5, getX() + getWidth() - 1, getY() + getHeight(), 0x6080788A);
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        int bx = getX() + getWidth() - 1;
        return mouseX >= bx - 1 && mouseX <= bx + 3
                && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    private double lerpScroll() {
        return Mth.lerp(Minecraft.getInstance().getFrameTime(), scrollSmoothO, scrollSmooth);
    }

    private double maxScroll() {
        return Math.max(0, contentHeight - getHeight());
    }

    private boolean scrollEnabled() {
        return contentHeight > getHeight();
    }
}