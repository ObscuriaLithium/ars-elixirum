package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static java.lang.Math.ceil;

public class TextControl extends HierarchicalControl {

    @Getter private Component content = Component.empty();
    @Getter private float scale = 1f;
    @Getter private boolean centered = false;

    private final Font font = Minecraft.getInstance().font;
    private MultiLineLabel label = MultiLineLabel.EMPTY;

    public static TextControl header(MutableComponent content) {
        TextControl node = new TextControl();
        node.setContent(content.withStyle(style -> style.withFont(ArsElixirum.FONT)));
        node.setCentered(true);
        node.setScale(1.2f);
        return node;
    }

    public static TextControl description(Component content, ChatFormatting color) {
        TextControl node = new TextControl();
        node.setContent(content.copy().withStyle(style -> style.withColor(color)));
        node.setScale(0.75f);
        return node;
    }

    public static TextControl description(Component content) {
        TextControl node = new TextControl();
        node.setContent(content.copy().withStyle(style -> style.withColor(ArsElixirumPalette.LIGHT.decimal())));
        node.setScale(0.75f);
        return node;
    }

    public static TextControl panelFooter(Component content) {
        TextControl node = new TextControl();
        node.setContent(content.copy().withStyle(style -> style.withColor(ArsElixirumPalette.LIGHT.decimal())));
        node.setCentered(true);
        node.setScale(0.66f);
        return node;
    }

    public static TextControl title(MutableComponent content) {
        TextControl node = new TextControl();
        node.setContent(content.withStyle(style -> style.withFont(ArsElixirum.FONT)));
        node.setCentered(true);
        return node;
    }

    public TextControl() {
        this(0, 0, 0, 0);
    }

    public TextControl(Component content) {
        this(0, 0, 0, 0);
        this.setContent(content);
    }

    public TextControl(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.setUpdateFlags(UPDATE_BY_WIDTH);
    }

    public void setContent(Component value) {
        this.content = value;
        this.isChanged = true;
    }

    public void setScale(float value) {
        this.scale = value;
        this.isChanged = true;
    }

    public void setCentered(boolean value) {
        this.centered = value;
        this.isChanged = true;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (!this.rect.visible()) return;

        graphics.pose().pushPose();

        if (this.centered) {
            graphics.pose().translate(rect.centerX(), rect.y(), 0);
            graphics.pose().scale(scale, scale, scale);
            label.renderCentered(graphics, 0, 0, 10, 0xFFFFFFFF);
        } else {
            graphics.pose().translate(rect.x(), rect.y(), 0);
            graphics.pose().scale(scale, scale, scale);
            label.renderLeftAligned(graphics, 0, 0, 10, 0xFFFFFFFF);
        }

        graphics.pose().popPose();
    }

    @Override
    public void reorganize() {
        this.label = MultiLineLabel.create(font, content, (int) (rect.width() / scale));
        this.rect.setHeight((int) ceil(((10 * label.getLineCount() - 1) * scale)));
    }

    @Override
    protected boolean hasContents() {
        return true;
    }
}