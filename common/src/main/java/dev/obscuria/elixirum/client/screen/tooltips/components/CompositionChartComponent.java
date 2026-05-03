package dev.obscuria.elixirum.client.screen.tooltips.components;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.api.alchemy.AlchemyProperties;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ChartElement;
import dev.obscuria.elixirum.client.screen.ChartRenderer;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CompositionChartComponent implements ClientTooltipComponent {

    private static final int CHART_SIZE = 32;
    private static final int LINE_H = 10;
    private static final int PAD_LEFT = 6;
    private static final RGB COLOR_GRAY;
    private static final Component UNKNOWN_ENTRY;

    private final ItemStack stack;
    private final Aspect aspect;
    private final List<ChartElement> elements;
    private final List<LineInfo> lines;

    public CompositionChartComponent(ItemStack stack, AlchemyProperties properties) {
        this.stack = stack;
        this.aspect = properties.aspect();
        this.elements = properties.streamSorted().map(this::entryToElement).toList();
        this.lines = properties.streamSorted().map(this::entryToLine).toList();
    }

    @Override
    public int getHeight() {
        int linesH = lines.stream().mapToInt(l -> LINE_H).sum();
        return Math.max(CHART_SIZE, linesH) + 4;
    }

    @Override
    public int getWidth(Font font) {
        int maxLineW = lines.stream().mapToInt(l -> l.getWidth(font)).max().orElse(0);
        return CHART_SIZE + PAD_LEFT + maxLineW;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        render(graphics, font, x, y, -1, -1);
    }

    public void render(GuiGraphics graphics, Font font,
                       int x, int y, int mouseX, int mouseY) {

        final int totalH = getHeight();
        final int totalW = getWidth(font);
        final var mat = graphics.pose().last().pose();

        RenderSystem.enableBlend();
        if (!elements.isEmpty()) {
            float totalW2 = (float) elements.stream().mapToDouble(ChartElement::weight).sum();
            float avgR = 0, avgG = 0, avgB = 0;
            for (var el : elements) {
                float w = el.weight() / totalW2;
                avgR += el.color().red() * w;
                avgG += el.color().green() * w;
                avgB += el.color().blue() * w;
            }
            final int ir = (int) avgR, ig = (int) avgG, ib = (int) avgB;
            final int cx = x + CHART_SIZE / 2;
            final int cy = y + 2 + CHART_SIZE / 2;

            graphics.drawManaged(() -> {
                var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                int r2 = CHART_SIZE / 2 + 2;
                buf.vertex(mat, cx - r2, cy - r2, 0f).color(ir, ig, ib, 0).endVertex();
                buf.vertex(mat, cx - r2, cy + r2, 0f).color(ir, ig, ib, 0).endVertex();
                buf.vertex(mat, cx + r2, cy + r2, 0f).color(ir, ig, ib, 0).endVertex();
                buf.vertex(mat, cx + r2, cy - r2, 0f).color(ir, ig, ib, 0).endVertex();

                int r1 = CHART_SIZE / 2 - 4;
                buf.vertex(mat, cx - r1, cy - r1, 0f).color(ir, ig, ib, 18).endVertex();
                buf.vertex(mat, cx - r1, cy + r1, 0f).color(ir, ig, ib, 18).endVertex();
                buf.vertex(mat, cx + r1, cy + r1, 0f).color(ir, ig, ib, 18).endVertex();
                buf.vertex(mat, cx + r1, cy - r1, 0f).color(ir, ig, ib, 18).endVertex();
            });
        }
        RenderSystem.disableBlend();

        ChartRenderer.renderChart(graphics, x, y + 2, CHART_SIZE, elements);
        graphics.blit(aspect.texture, x + 7, y + 9, 0, 0, 18, 18, 18, 18);

        int sepX = x + CHART_SIZE + PAD_LEFT / 2;
        {
            final var lmat = graphics.pose().last().pose();
            RenderSystem.enableBlend();
            graphics.drawManaged(() -> {
                var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                buf.vertex(lmat, sepX, y + 2, 0f).color(255, 255, 255, 0).endVertex();
                buf.vertex(lmat, sepX, y + 2 + totalH / 2, 0f).color(255, 255, 255, 35).endVertex();
                buf.vertex(lmat, sepX + 1, y + 2 + totalH / 2, 0f).color(255, 255, 255, 35).endVertex();
                buf.vertex(lmat, sepX + 1, y + 2, 0f).color(255, 255, 255, 0).endVertex();

                buf.vertex(lmat, sepX, y + 2 + totalH / 2, 0f).color(255, 255, 255, 35).endVertex();
                buf.vertex(lmat, sepX, y + 2 + totalH, 0f).color(255, 255, 255, 0).endVertex();
                buf.vertex(lmat, sepX + 1, y + 2 + totalH, 0f).color(255, 255, 255, 0).endVertex();
                buf.vertex(lmat, sepX + 1, y + 2 + totalH / 2, 0f).color(255, 255, 255, 35).endVertex();
            });
            RenderSystem.disableBlend();
        }

        int legendX = x + CHART_SIZE + PAD_LEFT;
        int legendY = y + 2 + Math.max(0, (CHART_SIZE - lines.size() * LINE_H) / 2);

        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            int lx = legendX;
            int ly = legendY + LINE_H * i;
            int col = line.color().decimal() & 0x00FFFFFF;
            boolean isHovered = mouseX >= lx && mouseX < lx + getWidth(font) - CHART_SIZE - PAD_LEFT
                    && mouseY >= ly && mouseY < ly + LINE_H;
            boolean isUnknown = line.color().equals(COLOR_GRAY);

            if (isHovered && !isUnknown) {
                int r = (col >> 16) & 0xFF, g = (col >> 8) & 0xFF, b = col & 0xFF;
                final int flx = lx - 1, fly = ly, flw = getWidth(font) - CHART_SIZE - PAD_LEFT + 1;
                final var hmat = graphics.pose().last().pose();
                RenderSystem.enableBlend();
                graphics.drawManaged(() -> {
                    var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    buf.vertex(hmat, flx, fly, 0f).color(r, g, b, 0).endVertex();
                    buf.vertex(hmat, flx, fly + LINE_H, 0f).color(r, g, b, 30).endVertex();
                    buf.vertex(hmat, flx + flw, fly + LINE_H, 0f).color(r, g, b, 30).endVertex();
                    buf.vertex(hmat, flx + flw, fly, 0f).color(r, g, b, 0).endVertex();
                });
                RenderSystem.disableBlend();
            }

            renderColorSwatch(graphics, lx + 1, ly + 2, col, isUnknown);

            int textAlpha = isUnknown ? 0x99 : (isHovered ? 0xFF : 0xCC);
            int textColor = (textAlpha << 24) | (isUnknown ? 0x888888 : 0xFFFFFF);
            graphics.drawString(font, line.component(), lx + 9, ly + 1, textColor, false);
        }
    }

    private void renderColorSwatch(GuiGraphics graphics, int x, int y, int col, boolean gray) {
        if (gray) {
            graphics.fill(x, y, x + 6, y + 5, 0xFF444455);
            return;
        }
        int r = (col >> 16) & 0xFF, g = (col >> 8) & 0xFF, b = col & 0xFF;
        final var mat = graphics.pose().last().pose();
        RenderSystem.enableBlend();
        graphics.drawManaged(() -> {
            var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
            buf.vertex(mat, x, y, 0f).color(r, g, b, 80).endVertex();
            buf.vertex(mat, x, y + 5, 0f).color(r, g, b, 80).endVertex();
            buf.vertex(mat, x + 6, y + 5, 0f).color(r, g, b, 255).endVertex();
            buf.vertex(mat, x + 6, y, 0f).color(r, g, b, 255).endVertex();
        });
        RenderSystem.disableBlend();
        graphics.fill(x + 5, y, x + 6, y + 5, 0xFF000000 | col);
    }

    private ChartElement entryToElement(Object2IntMap.Entry<EssenceHolder> entry) {
        return new ChartElement(
                isKnown(entry.getKey()) ? entry.getKey().color() : COLOR_GRAY,
                entry.getIntValue());
    }

    private LineInfo entryToLine(Object2IntMap.Entry<EssenceHolder> entry) {
        boolean known = isKnown(entry.getKey());
        return new LineInfo(
                known ? entry.getKey().color() : COLOR_GRAY,
                known ? formatLine(entry) : UNKNOWN_ENTRY);
    }

    private Component formatLine(Object2IntMap.Entry<EssenceHolder> entry) {
        return Component.translatable("tooltip.composition_entry.format",
                entry.getKey().displayName().getString(),
                entry.getIntValue()
        ).withStyle(ChatFormatting.GRAY);
    }

    private boolean isKnown(EssenceHolder holder) {
        return stack.is(ElixirumItems.EXTRACT.asItem())
                || DiscoverySystem.isEssenceKnown(
                ClientAlchemy.localProfile(), stack.getItem(), holder);
    }

    public record LineInfo(RGB color, Component component) {

        public int getHeight() {return LINE_H;}

        public int getWidth(Font font) {
            return 9 + font.width(component);
        }
    }

    static {
        COLOR_GRAY = Colors.rgbOf(5592405);
        UNKNOWN_ENTRY = Component
                .translatable("tooltip.composition_entry.unknown")
                .withStyle(ChatFormatting.DARK_GRAY);
    }
}
