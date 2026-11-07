package dev.obscuria.elixirum.client.screen.toolkit.tools;

import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;

public record Label(MultiLineLabel multiLine, float scale) {

    public static final Label EMPTY = new Label(MultiLineLabel.EMPTY, 1.0F);

    public static Label create(Component text, int maxWidth, float scale) {
        var snappedScale = GuiToolkit.snapScale(scale);
        var scaledMaxWidth = (int) Math.max(1, maxWidth / snappedScale);
        return new Label(MultiLineLabel.create(Minecraft.getInstance().font, text, scaledMaxWidth), snappedScale);
    }

    public boolean isEmpty() {
        return multiLine.getLineCount() <= 0;
    }

    public int getHeight() {
        if (multiLine.getLineCount() <= 0) return 0;
        return (int) Math.ceil(11 * multiLine.getLineCount() * scale);
    }

    public void draw(GuiGraphics graphics, float x, float y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1);
        this.multiLine.renderLeftAligned(graphics, 0, 0, 10, color);
        graphics.pose().popPose();
    }

    public void drawHCentered(GuiGraphics graphics, float x, float y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1);
        this.multiLine.renderCentered(graphics, 0, 0, 10, color);
        graphics.pose().popPose();
    }

    public void drawVCentered(GuiGraphics graphics, float x, float y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, 0, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.pose().translate(0, y - getHeight() * 0.5f, 0);
        this.multiLine.renderLeftAligned(graphics, 0, 0, 10, color);
        graphics.pose().popPose();
    }

    public void drawHVCentered(GuiGraphics graphics, float x, float y, int color) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.pose().translate(0, getHeight() * -0.5f, 0);
        this.multiLine.renderCentered(graphics, 0, 0, 10, color);
        graphics.pose().popPose();
    }
}
