package dev.obscuria.elixirum.client.screen;

import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class HierarchicalWidget extends AbstractWidget {
    protected static final int UPDATE_BY_WIDTH = 1;
    protected static final int UPDATE_BY_HEIGHT = 1 << 1;
    private final List<HierarchicalWidget> children = Lists.newArrayList();
    private @Nullable ClickAction<?> clickAction;
    private @Nullable Holder<SoundEvent> clickSound;
    private boolean changed = true;
    private int updateFlags;

    protected HierarchicalWidget(int x, int y, int width, int height, Component name) {
        super(x, y, width, height, name);
    }

    public abstract void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY);

    protected abstract void reorganize();

    public boolean mouseClicked(GlobalTransform transform, double mouseX, double mouseY, int button) {
        if (transform.isMouseOver(mouseX, mouseY)
                && clickAction != null
                && clickAction.canConsume(button)
                && clickAction.invokeCast(this)) {
            this.playClickSound();
            return true;
        }
        for (var child : children())
            if (child.mouseClicked(transform.child(child), mouseX, mouseY, button))
                return true;
        return false;
    }

    public void tick() {
        for (var child : children())
            child.tick();
    }

    public boolean mouseScrolled(GlobalTransform transform, double mouseX, double mouseY, double scrollX, double scrollY) {
        for (var child : children())
            if (child.mouseScrolled(transform.child(child), mouseX, mouseY, scrollX, scrollY))
                return true;
        return false;
    }

    public <T extends HierarchicalWidget> T addChild(T widget) {
        this.children.add(widget);
        this.setChanged(true);
        return widget;
    }

    public List<HierarchicalWidget> children() {
        return this.children;
    }

    public final HierarchicalWidget setClickAction(ClickAction<?> action) {
        this.clickAction = action;
        return this;
    }

    public final HierarchicalWidget setClickSound(Holder<SoundEvent> sound) {
        this.clickSound = sound;
        return this;
    }

    protected final void setUpdateFlags(int flags) {
        this.updateFlags = flags;
    }

    protected final boolean hasUpdateFlag(int flag) {
        return (this.updateFlags & flag) == flag;
    }

    protected final void setChanged(boolean value) {
        this.changed = value;
    }

    protected final boolean isChanged() {
        return this.changed;
    }

    protected final boolean isAnyChanged() {
        if (isChanged()) return true;
        for (var child : children())
            if (child.isAnyChanged())
                return true;
        return false;
    }

    protected final void consumeChanges() {
        this.children().forEach(HierarchicalWidget::consumeChanges);
        this.setChanged(false);
        this.reorganize();
    }

    protected final void defaultRender(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        for (var child : children())
            child.render(graphics, transform.child(child), mouseX, mouseY);
    }

    protected final void playClickSound() {
        if (clickSound == null) return;
        final var manager = Minecraft.getInstance().getSoundManager();
        manager.play(SimpleSoundInstance.forUI(clickSound.value(), 1.0F));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected final void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    public void setX(int value) {
        final var difference = value - getX();
        super.setX(value);
        for (var child : children())
            child.setX(child.getX() + difference);
    }

    @Override
    public void setY(int value) {
        final var difference = value - getY();
        super.setY(value);
        for (var child : children())
            child.setY(child.getY() + difference);
    }

    @Override
    public void setWidth(int width) {
        if (this.hasUpdateFlag(UPDATE_BY_WIDTH) && getWidth() != width)
            this.setChanged(true);
        super.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        if (this.hasUpdateFlag(UPDATE_BY_HEIGHT) && getHeight() != height)
            this.setChanged(true);
        super.setHeight(height);
    }
}
