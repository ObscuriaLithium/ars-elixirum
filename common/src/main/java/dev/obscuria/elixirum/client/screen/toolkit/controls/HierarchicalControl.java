package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.MouseFilter;
import dev.obscuria.elixirum.client.screen.toolkit.Rectangle;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class HierarchicalControl extends AbstractWidget {

    public static final byte UPDATE_BY_WIDTH = 1;
    public static final byte UPDATE_BY_HEIGHT = 1 << 1;

    public final Rectangle rect = new Rectangle(this);
    public MouseFilter mouseFilter = MouseFilter.STOP;
    public @Nullable ClickAction<?> clickAction;
    public @Nullable SoundEvent clickSound = ElixirumSounds.UI_CLICK_1;
    public boolean isChanged = true;

    private final List<HierarchicalControl> children = new ArrayList<>();
    private byte internalUpdateFlags;

    public HierarchicalControl(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public abstract void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY);

    public abstract void reorganize();

    public Stream<HierarchicalControl> listChildren() {
        return Arrays.stream(children.toArray(HierarchicalControl[]::new));
    }

    public boolean hasChildren() {
        return listChildren().findAny().isPresent();
    }

    public <T extends HierarchicalControl> T addChild(T node) {
        children.add(node);
        isChanged = true;
        return node;
    }

    public void removeChild(HierarchicalControl node) {
        children.remove(node);
        isChanged = true;
    }

    public void clearChildren() {
        children.clear();
        isChanged = true;
    }

    public void tick() {
        listChildren().forEach(HierarchicalControl::tick);
    }

    public boolean mouseClicked(GlobalTransform transform, double mouseX, double mouseY, int button) {
        if (!rect.visible()) return false;
        if (active
                && mouseFilter != MouseFilter.IGNORE
                && clickAction != null
                && clickAction.mouseClicked(this, transform, mouseX, mouseY, button)) {
            playClickSound();
            if (mouseFilter == MouseFilter.STOP) return true;
        }
        return listChildren().anyMatch(it -> it.mouseClicked(transform.forChild(it), mouseX, mouseY, button));
    }

    public boolean mouseScrolled(GlobalTransform transform, double mouseX, double mouseY, double scroll) {
        if (!rect.visible()) return false;
        return listChildren().anyMatch(it -> it.mouseScrolled(transform.forChild(it), mouseX, mouseY, scroll));
    }

    public void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    public void setHeight(int value) {
        this.height = value;
    }

    public void setWidth(int value) {
        this.width = value;
    }

    public void setUpdateFlags(byte flags) {
        this.internalUpdateFlags = flags;
    }

    public boolean hasUpdateFlag(byte flag) {
        return (internalUpdateFlags & flag) != 0;
    }

    protected boolean hasContents() {
        return false;
    }

    protected boolean hasVisibleContents() {
        if (rect.visible() && hasContents()) return true;
        return listChildren().anyMatch(HierarchicalControl::hasVisibleContents);
    }

    protected boolean isAnyChanged() {
        return isChanged || listChildren().anyMatch(HierarchicalControl::isAnyChanged);
    }

    protected void consumeChanges() {
        listChildren().forEach(HierarchicalControl::consumeChanges);
        isChanged = false;
        reorganize();
    }

    protected void playClickSound() {
        if (clickSound == null) return;
        playSound(clickSound);
    }

    protected void renderChildren(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (!rect.visible()) return;
        listChildren().forEach(child -> child.render(graphics, transform.forChild(child), mouseX, mouseY));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {}

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
