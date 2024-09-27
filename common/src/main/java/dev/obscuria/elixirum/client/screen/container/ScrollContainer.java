package dev.obscuria.elixirum.client.screen.container;

import dev.obscuria.elixirum.client.screen.ElixirumPalette;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public final class ScrollContainer extends HierarchicalWidget {
    private @Nullable MultiLineLabel placeholderLabel;
    private double scrollValue;
    private double scrollValueO;
    private double scroll;
    private int childrenHeight;
    private int visibleHeight;

    public ScrollContainer(Component placeholder) {
        super(0, 0, 0, 0, placeholder);
        this.setUpdateFlags(UPDATE_BY_HEIGHT);
    }

    public void resetScroll() {
        this.scrollValue = 0;
        this.scrollValueO = 0;
        this.scroll = 0;
        consumeChanges();
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        ElixirumScreen.debugRenderer(this, graphics, transform, mouseX, mouseY);
        final var scroll = getScroll();

        graphics.enableScissor(transform.rect().left(), transform.rect().top(), transform.rect().right(), transform.rect().bottom());
        graphics.pose().pushPose();
        graphics.pose().translate(0, -scroll, 0);
        for (var child : children())
            child.render(graphics, GlobalTransform.offset(child, transform.rect(), 0, (int) -scroll), mouseX, mouseY);
        graphics.pose().popPose();
        graphics.disableScissor();

        if (isScrollEnabled()) {
            graphics.fill(getRight() - 1, getY(), getRight() + 1, getBottom(), 0x2F000000);
            final var clampedScroll = Math.clamp(scroll, 0, getMaxScroll());
            final var ratio = clampedScroll / 1f / getMaxScroll();
            final var height = (int) (getHeight() * (visibleHeight / 1f / childrenHeight));
            final var offset = (getHeight() - height) * ratio;
            graphics.pose().pushPose();
            graphics.pose().translate(getRight() - 1, getY() + offset, 0);
            graphics.fill(0, 0, 2, height, ElixirumPalette.purple(100));
            graphics.pose().popPose();
        }

        if (!hasVisibleContents() && placeholderLabel != null) {
            final var x = getX() + getWidth() / 2;
            final var y = getY() + getHeight() / 2 - placeholderLabel.getLineCount() * 5;
            placeholderLabel.renderCentered(graphics, x, y);
        }
    }

    @Override
    public void tick() {
        this.scrollValueO = scrollValue;
        this.scrollValue = Mth.lerp(0.5, scrollValue, scroll);
        if (scroll < 0 || !isScrollEnabled()) {
            this.scroll = Mth.lerp(0.4, scroll, 0.0);
        } else if (scroll > getMaxScroll()) {
            this.scroll = Mth.lerp(0.4, scroll, getMaxScroll());
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(GlobalTransform transform, double mouseX, double mouseY, int button) {
        final var scroll = getScroll();
        for (var child : children())
            if (child.mouseClicked(GlobalTransform.offset(child, transform.rect(), 0, (int) -scroll), mouseX, mouseY, button))
                return true;
        return false;
    }

    @Override
    public boolean mouseScrolled(GlobalTransform transform, double mouseX, double mouseY, double scrollX, double scrollY) {
        if (transform.isMouseOver(mouseX, mouseY)) {
            if (!isScrollEnabled()) return false;
            this.scroll += scrollY * -20.0;
            final var manager = Minecraft.getInstance().getSoundManager();
            manager.play(SimpleSoundInstance.forUI(ElixirumSounds.UI_SCROLL, 1f));
            return true;
        }
        return super.mouseScrolled(transform, mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void reorganize() {
        this.visibleHeight = getHeight();
        this.childrenHeight = 0;
        for (var child : children()) {
            child.setX(getX());
            child.setY(getY() + 5);
            child.setWidth(getWidth() - 2);
            this.childrenHeight = Math.max(childrenHeight + 10, child.getHeight() + 10);
        }
        final var font = Minecraft.getInstance().font;
        final var placeholder = getMessage().copy().withStyle(Style.EMPTY.withColor(ElixirumPalette.LIGHT));
        this.placeholderLabel = MultiLineLabel.create(font, placeholder, getWidth() - 10);
    }

    private boolean isScrollEnabled() {
        return childrenHeight > visibleHeight;
    }

    private double getScroll() {
        final var delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        return Mth.lerp(delta, scrollValueO, scrollValue);
    }

    private double getMaxScroll() {
        return childrenHeight - visibleHeight;
    }
}
