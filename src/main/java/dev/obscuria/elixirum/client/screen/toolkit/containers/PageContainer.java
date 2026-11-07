package dev.obscuria.elixirum.client.screen.toolkit.containers;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.Nullable;

public class PageContainer extends Control {

    private static final int BORDERS = 14;
    private static final int SLOT_PADDING = 5;

    private int headerBottom = 0;
    private int footerTop = 0;

    private @Nullable Control header;
    private @Nullable Control body;
    private @Nullable Control footer;

    public PageContainer(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);
    }

    public <T extends Control> T setHeader(T control) {
        if (header != null) removeChild(header);
        header = add(control);
        return control;
    }

    public <T extends Control> T setBody(T control) {
        if (body != null) removeChild(body);
        body = add(control);
        return control;
    }

    public <T extends Control> T setFooter(T control) {
        if (footer != null) removeChild(footer);
        footer = add(control);
        return control;
    }

    public void removeHeader() {
        if (header != null) {
            removeChild(header);
            header = null;
        }
    }

    public void removeBody() {
        if (body != null) {
            removeChild(body);
            body = null;
        }
    }

    public void removeFooter() {
        if (footer != null) {
            removeChild(footer);
            footer = null;
        }
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {

        RenderSystem.enableBlend();
        GuiToolkit.draw(graphics, Textures.PANEL, this);
        RenderSystem.disableBlend();

        renderChildren(graphics, context, mouseX, mouseY);

        if (header != null)
            graphics.hLine(
                    getX() + BORDERS - 1,
                    getX() + getWidth() - BORDERS,
                    headerBottom + 2, 0xFF80788A);

        if (footer != null)
            graphics.hLine(
                    getX() + BORDERS - 1,
                    getX() + getWidth() - BORDERS,
                    footerTop - 3, 0xFF80788A);
    }

    @Override
    protected void measure() {
        setMeasuredSize(0, 0);
    }

    @Override
    protected void layout() {
        int cw = getWidth() - BORDERS * 2;
        int innerTop = getY() + BORDERS;
        int innerBot = getY() + getHeight() - BORDERS;

        int usedTop = 0;
        if (header != null) {
            placeChild(header, getX() + BORDERS, innerTop, cw, header.getMeasuredHeight());
            usedTop = header.getHeight() + SLOT_PADDING;
        }

        this.headerBottom = innerTop + usedTop - (header != null ? SLOT_PADDING - 1 : 0);

        int usedBot = 0;
        if (footer != null) {
            int fh = footer.getMeasuredHeight();
            placeChild(footer, getX() + BORDERS, innerBot - fh, cw, fh);
            usedBot = fh + SLOT_PADDING;
        }
        this.footerTop = innerBot - usedBot + (footer != null ? SLOT_PADDING - 1 : 0);

        if (body != null) {
            int bodyY = innerTop + usedTop;
            int bodyH = innerBot - usedBot - bodyY;
            placeChild(body, getX() + BORDERS, bodyY, cw, bodyH);
        }
    }
}
