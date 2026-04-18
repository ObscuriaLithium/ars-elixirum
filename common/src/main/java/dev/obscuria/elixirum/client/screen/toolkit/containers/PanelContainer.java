package dev.obscuria.elixirum.client.screen.toolkit.containers;

import com.google.common.collect.Streams;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.Region;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class PanelContainer extends HierarchicalControl {

    private static final int BORDERS = 14;

    private int headerHeight;
    private int footerHeight;

    private @Nullable HierarchicalControl header;
    private @Nullable HierarchicalControl body;
    private @Nullable HierarchicalControl footer;

    public PanelContainer(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public <T extends HierarchicalControl> T setHeader(T control) {
        this.header = control;
        this.isChanged = true;
        return control;
    }

    public <T extends HierarchicalControl> T setBody(T control) {
        this.body = control;
        this.isChanged = true;
        return control;
    }

    public <T extends HierarchicalControl> T setFooter(T control) {
        this.footer = control;
        this.isChanged = true;
        return control;
    }

    public void removeHeader() {
        this.header = null;
        this.isChanged = true;
    }

    public void removeBody() {
        this.body = null;
        this.isChanged = true;
    }

    public void removeFooter() {
        this.footer = null;
        this.isChanged = true;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        while (isAnyChanged()) consumeChanges();

        RenderSystem.enableBlend();
        GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.PANEL, this);
        RenderSystem.disableBlend();

        this.renderChildren(graphics, transform, mouseX, mouseY);

        if (header != null) {
            graphics.hLine(rect.x() + BORDERS - 1, rect.right() - BORDERS, rect.y() + BORDERS + headerHeight - 1, 0xFF80788A);
        }

        if (footer != null) {
            graphics.hLine(rect.x() + BORDERS - 1, rect.right() - BORDERS, rect.bottom() - BORDERS - footerHeight, 0xFF80788A);
        }
    }

    @Override
    public void reorganize() {
        final var childWidth = getWidth() - BORDERS * 2;

        this.headerHeight = 0;
        if (header != null) {
            header.rect.setX(rect.x() + BORDERS);
            header.rect.setY(rect.y() + BORDERS);
            header.rect.setWidth(childWidth);
            this.headerHeight = header.rect.height() + 5;
        }

        this.footerHeight = 0;
        if (footer != null) {
            final var height = footer.rect.height();
            footer.rect.setX(rect.x() + BORDERS);
            footer.rect.setY(rect.bottom() - BORDERS - height);
            footer.rect.setWidth(childWidth);
            this.footerHeight = height + 5;
        }

        if (body != null) {
            body.rect.setX(rect.x() + BORDERS);
            body.rect.setY(rect.y() + BORDERS + headerHeight);
            body.rect.setWidth(childWidth);
            body.rect.setHeight(rect.height() - BORDERS * 2 - headerHeight - footerHeight);
        }
    }

    @Override
    public Stream<HierarchicalControl> listChildren() {
        return Streams.concat(super.listChildren(), Stream.of(header, body, footer).filter(Objects::nonNull));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.mouseClicked(GlobalTransform.of(this, Region.of(this)), mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return this.mouseScrolled(GlobalTransform.of(this, Region.of(this)), mouseX, mouseY, scroll);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.render(graphics, GlobalTransform.of(this, Region.of(this)), mouseX, mouseY);
    }
}
