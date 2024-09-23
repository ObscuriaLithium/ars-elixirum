package dev.obscuria.elixirum.client.screen.container;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.tool.Rect;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PanelContainer extends HierarchicalWidget {
    private static final int BORDER = 14;
    private @Nullable HierarchicalWidget headerChild;
    private @Nullable HierarchicalWidget footerChild;
    private @Nullable HierarchicalWidget contentChild;
    private int headerHeight;
    private int footerHeight;

    public PanelContainer(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public <T extends HierarchicalWidget> T setHeader(T widget) {
        this.headerChild = widget;
        this.setChanged(true);
        return widget;
    }

    public <T extends HierarchicalWidget> T setFooter(T widget) {
        this.footerChild = widget;
        this.setChanged(true);
        return widget;
    }

    public <T extends HierarchicalWidget> T setContent(T widget) {
        this.contentChild = widget;
        this.setChanged(true);
        return widget;
    }

    @Override
    public List<HierarchicalWidget> children() {
        final var result = Lists.<HierarchicalWidget>newArrayList();
        if (headerChild != null) result.add(headerChild);
        if (contentChild != null) result.add(contentChild);
        if (footerChild != null) result.add(footerChild);
        return result;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        while (isAnyChanged()) consumeChanges();

        RenderSystem.enableBlend();
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL, getX(), getY(), getWidth(), getHeight());
        RenderSystem.disableBlend();

        this.defaultRender(graphics, transform, mouseX, mouseY);

        if (this.headerChild != null)
            graphics.hLine(getX() + BORDER - 1, getRight() - BORDER, getY() + BORDER + headerHeight - 1, 0xFF80788A);
        if (this.footerChild != null)
            graphics.hLine(getX() + BORDER - 1, getRight() - BORDER, getBottom() - BORDER - footerHeight, 0xFF80788A);
    }

    @Override
    protected void reorganize() {
        final var childWidth = getWidth() - BORDER * 2;

        this.headerHeight = 0;
        if (this.headerChild != null) {
            this.headerChild.setX(getX() + BORDER);
            this.headerChild.setY(getY() + BORDER);
            this.headerChild.setWidth(childWidth);
            this.headerHeight = headerChild.getHeight() + 5;
        }

        this.footerHeight = 0;
        if (this.footerChild != null) {
            final var height = footerChild.getHeight();
            this.footerChild.setX(getX() + BORDER);
            this.footerChild.setY(getBottom() - BORDER - height);
            this.footerChild.setWidth(childWidth);
            this.footerHeight = footerChild.getHeight() + 5;
        }

        if (this.contentChild != null) {
            this.contentChild.setX(getX() + BORDER);
            this.contentChild.setY(getY() + BORDER + headerHeight);
            this.contentChild.setWidth(childWidth);
            this.contentChild.setHeight(getHeight() - BORDER * 2 - headerHeight - footerHeight);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.mouseClicked(GlobalTransform.of(this, Rect.of(this)), mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        return this.mouseScrolled(GlobalTransform.of(this, Rect.of(this)), pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.render(graphics, GlobalTransform.of(this, Rect.of(this)), mouseX, mouseY);
    }
}
