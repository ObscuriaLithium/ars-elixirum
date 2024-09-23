package dev.obscuria.elixirum.client.screen.container;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.Accessor;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpoilerContainer extends HierarchicalWidget {
    private static final ResourceLocation ARROW_UP = Elixirum.key("icon/arrow_up");
    private static final ResourceLocation ARROW_DOWN = Elixirum.key("icon/arrow_down");
    private boolean defaultExpanded;
    private Accessor<Boolean> accessor;

    public SpoilerContainer(Component name) {
        super(0, 0, 0, 0, name);
        this.setClickSound(ElixirumSounds.UI_CLICK_1.holder());
        this.accessor = Accessor.create(
                () -> defaultExpanded,
                value -> defaultExpanded = value);
    }

    public SpoilerContainer setAccessor(Accessor<Boolean> accessor) {
        this.accessor = accessor;
        return this;
    }

    public boolean isExpanded() {
        return this.accessor.get();
    }

    public void setExpanded(boolean value) {
        if (this.isExpanded() == value) return;
        this.accessor.set(value);
        this.setChanged(true);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        final var headerHovered = transform.isMouseOver(mouseX, mouseY) && mouseY <= transform.rect().top() + 14;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, headerHovered ? 0.4f : 0.2f);
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_LIGHT, getX(), getY(), getWidth(), 12);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        graphics.drawString(Minecraft.getInstance().font, getMessage(), getX() + 2, getY() + 2, 0xFFFFFFFF);
        graphics.blitSprite(isExpanded() ? ARROW_UP : ARROW_DOWN, getRight() - 10, getY() + 3, 8, 8);
        if (isExpanded()) this.defaultRender(graphics, transform, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(GlobalTransform transform, double mouseX, double mouseY, int button) {
        if (transform.isMouseOver(mouseX, mouseY)
                && mouseY <= transform.rect().top() + 14
                && button == 0) {
            this.playClickSound();
            this.setExpanded(!isExpanded());
            return true;
        }
        return isExpanded() && super.mouseClicked(transform, mouseX, mouseY, button);
    }

    @Override
    protected void reorganize() {
        var maxHeight = 0;
        for (var child : children()) {
            child.setX(getX());
            child.setY(getY() + 14);
            child.setWidth(getWidth());
            maxHeight = Math.max(maxHeight, child.getHeight());
        }
        this.setHeight(14 + (isExpanded() ? maxHeight : 0));
    }
}
