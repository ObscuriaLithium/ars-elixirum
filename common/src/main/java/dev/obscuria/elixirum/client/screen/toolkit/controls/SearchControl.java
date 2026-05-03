package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Texture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SearchControl extends LineEditControl {

    private static final int ICON_RESERVED = 14;
    private static final int ICON_SIZE = 7;

    private @Nullable Texture iconTexture = null;

    public SearchControl() {
        super();
        setPlaceholder(Component.literal("Search..."));
        setScale(0.75f);
    }

    public static SearchControl withHandler(Component placeholder,
                                            Consumer<String> onChanged) {
        var ctrl = new SearchControl();
        ctrl.setPlaceholder(placeholder);
        ctrl.setOnChanged(onChanged);
        return ctrl;
    }

    public void setIconTexture(@Nullable Texture iconTexture) {
        this.iconTexture = iconTexture;
    }

    @Override
    protected int innerWidth() {
        return (int) ((getWidth() - PAD_X * 2 - ICON_RESERVED) / scale);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (!context.isWithinScissor(this)) return;
        boolean hovered = active && context.isMouseOver(this, mouseX, mouseY);

        if (!active) context.pushAlpha(0.33f);
        GuiToolkit.draw(graphics, pickTexture(hovered), this);
        if (!active) context.popModulate();

        renderTextContent(graphics, context, mouseX, mouseY);
        renderIcon(graphics, context, hovered);
    }

    protected void renderIcon(GuiGraphics graphics, GuiContext context, boolean hovered) {
        boolean lit = isFocused() || hovered;

        int iconPx = (int) Math.ceil(ICON_SIZE * scale);
        int marginPx = (int) Math.ceil((ICON_RESERVED - ICON_SIZE) / 2f * scale);
        int iconX = getX() + getWidth() - marginPx - iconPx;
        int iconY = getY() + (getHeight() - iconPx) / 2;

        if (iconTexture != null) {
            float t = lit ? 1f : 0xAA / 255f;
            GuiToolkit.setShaderColor(t, t, t, 1f);
            iconTexture.render(graphics, iconX, iconY, iconPx, iconPx);
            GuiToolkit.resetShaderColor();
        } else {
            drawPixelMagnifier(graphics, iconX, iconY, iconPx, lit);
        }
    }

    private void drawPixelMagnifier(GuiGraphics graphics,
                                    int x, int y, int totalPx, boolean lit) {
        int color = lit ? 0xFFFFFFFF : Palette.LIGHT.decimal();

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, scale);

        int s = ICON_SIZE;

        graphics.fill(1, -1, s - 2, 0, color);
        graphics.fill(1, 3, s - 2, 4, color);
        graphics.fill(0, 0, 1, 3, color);
        graphics.fill(s - 2, 0, s - 1, 3, color);
        graphics.fill(s - 2, s - 3, s - 1, s - 2, color);
        graphics.fill(s - 1, s - 2, s, s - 1, color);

        graphics.pose().popPose();
    }
}