package dev.obscuria.elixirum.client.screen.container;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public final class ScrollContainer extends HierarchicalWidget {
    private double scrollValue;
    private double scrollValueO;
    private double scroll;
    private int childrenHeight;
    private int visibleHeight;

    public ScrollContainer() {
        super(0, 0, 0, 0, Component.empty());
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
    }

    @Override
    public void tick() {
        this.scrollValueO = scrollValue;
        this.scrollValue = Mth.lerp(0.5, scrollValue, scroll);
        final var maxScroll = childrenHeight - visibleHeight;
        if (scroll < 0 || childrenHeight <= visibleHeight) {
            this.scroll = Mth.lerp(0.4, scroll, 0.0);
        } else if (scroll > maxScroll) {
            this.scroll = Mth.lerp(0.4, scroll, maxScroll);
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
            if (visibleHeight >= childrenHeight) return false;
            this.scroll += scrollY * -20.0;
            final var manager = Minecraft.getInstance().getSoundManager();
            manager.play(SimpleSoundInstance.forUI(ElixirumSounds.UI_SCROLL.value(), 1f));
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
            child.setWidth(getWidth());
            this.childrenHeight = Math.max(childrenHeight + 10, child.getHeight() + 10);
        }
    }

    private double getScroll() {
        final var delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        return Mth.lerp(delta, scrollValueO, scrollValue);
    }
}
