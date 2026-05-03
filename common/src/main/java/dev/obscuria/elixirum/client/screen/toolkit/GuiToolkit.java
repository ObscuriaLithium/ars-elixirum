package dev.obscuria.elixirum.client.screen.toolkit;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.ArsElixirumClient;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Texture;
import dev.obscuria.fragmentum.util.color.ARGB;
import dev.obscuria.fragmentum.util.color.RGB;
import dev.obscuria.fragmentum.world.tooltip.TooltipOptions;
import dev.obscuria.fragmentum.world.tooltip.Tooltips;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface GuiToolkit {

    TooltipOptions FORMAT_OPTIONS = TooltipOptions.builder()
            .withMaxLineLength(Integer.MAX_VALUE)
            .build();

    String HIGHLIGHT_PREFIX = "[color=#ffaa00]";
    String HIGHLIGHT_SUFFIX = "[/color]";

    float HEADER_SCALE = 1.25f;
    float SUBHEADER_SCALE = 1.0f;
    float PARAGRAPH_SCALE = 0.75f;
    float FOOTNOTE_SCALE = 0.66f;

    static float snapScale(float desiredScale) {
        int guiScale = Minecraft.getInstance().options.guiScale().get();
        if (guiScale <= 0) return desiredScale;
        float scaled = desiredScale * guiScale;
        int k = Math.round(scaled);
        if (k <= 0) k = 1;
        return (float) k / guiScale;
    }

    static Component format(Component text) {
        var result = Tooltips.process(text, GuiToolkit.class, FORMAT_OPTIONS);
        return result.isEmpty() ? CommonComponents.EMPTY : result.get(0);
    }

    static Component highlight(String input, String filter) {
        if (filter.isBlank()) return Component.literal(input);
        var pattern = Pattern.compile("(?i)" + Pattern.quote(filter));
        var matcher = pattern.matcher(input);
        var result = new StringBuilder(input.length() + 32);
        while (matcher.find()) {
            var match = matcher.group();
            var replacement = HIGHLIGHT_PREFIX + Matcher.quoteReplacement(match) + HIGHLIGHT_SUFFIX;
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return GuiToolkit.format(Component.literal(result.toString()));

    }

    static MutableComponent applyFont(Component text) {
        return text.copy().withStyle(style -> style.withFont(ArsElixirum.FONT));
    }

    static MutableComponent dye(Component text, ChatFormatting color) {
        return text.copy().withStyle(style -> style.withColor(color));
    }

    static MutableComponent dye(Component text, ARGB color) {
        return text.copy().withStyle(style -> style.withColor(color.decimal()));
    }

    static void draw(GuiGraphics graphics, Texture texture, Control control) {
        draw(graphics, texture, control, 0, 0, 0, 0);
    }

    static void draw(GuiGraphics graphics, Texture texture, Control control, int x, int y, int width, int height) {
        draw(graphics, texture, control.getX() + x, control.getY() + y, control.getWidth() + width, control.getHeight() + height);
    }

    static void draw(GuiGraphics graphics, Texture texture, int x, int y, int width, int height) {
        texture.render(graphics, x, y, width, height);
    }

    static void draw(GuiGraphics graphics, GuiContext context, Texture texture, Control control, float alpha) {
        RenderSystem.enableBlend();
        context.pushAlpha(alpha);
        draw(graphics, texture, control);
        context.popModulate();
        RenderSystem.disableBlend();
    }

    static void setShaderColor(RGB color) {
        setShaderColor(color, 1f);
    }

    static void setShaderColor(RGB color, float alpha) {
        setShaderColor(color.red(), color.green(), color.blue(), alpha);
    }

    static void setShaderColor(ARGB color) {
        setShaderColor(color, color.alpha());
    }

    static void setShaderColor(ARGB color, float alpha) {
        setShaderColor(color.red(), color.green(), color.blue(), alpha);
    }

    static void setShaderColor(float red, float green, float blue, float alpha) {
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    static void resetShaderColor() {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    static void draw(MultiLineLabel label, GuiGraphics graphics, int x, int y, int separation, ARGB color) {
        label.renderLeftAligned(graphics, x, y, separation, color.decimal());
    }

    static void drawBubbleParallax(GuiGraphics graphics, GuiContext context, Control control) {

        var x = control.getX();
        var y = control.getY();
        var width = control.getWidth();
        var height = control.getHeight();

        RenderSystem.enableBlend();
        context.pushAlpha(0.25f);
        GuiToolkit.draw(graphics, Textures.SOLID_PURPLE, control);
        context.popModulate();
        var timer = ArsElixirumClient.timer();

        context.pushModulate(Palette.ACCENT, 0.1f);
        graphics.blit(Textures.BUBBLES, x, y, width, height,
                x + (float) Math.sin(timer) * 7.5f,
                y - (float) context.scrollOffsetY() + timer * 10f,
                width, height, 64, 64);
        context.popModulate();

        context.pushModulate(Palette.ACCENT, 0.3f);
        graphics.blit(Textures.BUBBLES, x, y, width, height,
                x + (float) Math.cos(timer) * 15f,
                y - (float) context.scrollOffsetY() + timer * 20f,
                width, height, 64, 64);
        context.popModulate();

        GuiToolkit.draw(graphics, Textures.OUTLINE_PURPLE, control);
        RenderSystem.disableBlend();
    }
}
