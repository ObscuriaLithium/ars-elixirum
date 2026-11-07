package dev.obscuria.elixirum.client.screen.alchemy.controls;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.alchemy.guide.contents.TableBlock;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TableControl extends Control {

    private final float headerScale;
    private final float contentScale;

    private record CellData(
            @Nullable MultiLineLabel label,
            @Nullable ResourceLocation icon,
            int color
    ) {

        static CellData text(MultiLineLabel label, int color) {
            return new CellData(label, null, color);
        }

        static CellData icon(ResourceLocation icon) {
            return new CellData(null, icon, 0xFFFFFFFF);
        }

        boolean isIcon() {
            return icon != null;
        }
    }

    private static final int LINE_SPACING = 10;
    private static final int GLYPH_HEIGHT = 8;
    private static final int STRIPE_COLOR = Palette.DARKEST.withAlpha(0.66f).decimal();

    private final TableBlock block;
    private final Font font = Minecraft.getInstance().font;

    private int[] columnWidths = new int[0];
    private int headerHeight = 0;
    private int[] rowHeights = new int[0];
    private List<CellData> headerCells = List.of();
    private List<List<CellData>> contentCells = List.of();

    public TableControl(TableBlock block) {
        this.block = block;
        this.headerScale = GuiToolkit.snapScale(block.headerScale());
        this.contentScale = GuiToolkit.snapScale(block.contentScale());
        setSizeHints(SIZE_HINT_WIDTH);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (block.columns().isEmpty()) return;

        context.pushModulate(Palette.DARKEST);
        GuiToolkit.draw(graphics, Textures.OUTLINE_WHITE, this);
        context.popModulate();

        if (block.striped()) {
            int stripeY = getY() + headerHeight;
            for (int r = 0; r < rowHeights.length; r++) {
                if (r % 2 == 0) {
                    graphics.fill(getX(), stripeY,
                            getX() + getWidth(), stripeY + rowHeights[r],
                            STRIPE_COLOR);
                }
                stripeY += rowHeights[r];
            }
        }

        int lineColor = Palette.DARKEST.decimal();

        int vx = getX();
        for (int col = 0; col < columnWidths.length - 1; col++) {
            vx += columnWidths[col];
        }

        int separatorY = getY() + headerHeight;
        graphics.hLine(getX(), getX() + getWidth() - 1, separatorY, lineColor);
        if (block.headerThickSeparator()) {
            graphics.hLine(getX(), getX() + getWidth() - 1, separatorY - 1, lineColor);
        }

        int hy = getY() + headerHeight;
        for (int r = 0; r < rowHeights.length - 1; r++) {
            hy += rowHeights[r];
            graphics.hLine(getX(), getX() + getWidth() - 1, hy, lineColor);
        }

        renderRow(graphics, headerCells, getY(), headerHeight, headerScale);

        int offsetY = getY() + headerHeight;
        for (int r = 0; r < contentCells.size(); r++) {
            renderRow(graphics, contentCells.get(r), offsetY, rowHeights[r], contentScale);
            offsetY += rowHeights[r];
        }
    }

    private void renderRow(
            GuiGraphics graphics,
            List<CellData> cells,
            int rowY,
            int rowHeight,
            float scale
    ) {
        int offsetX = getX();
        int hPadding = block.hPadding();
        int vPadding = block.vPadding();

        for (int col = 0; col < cells.size(); col++) {
            var cell = cells.get(col);
            var colDef = block.columns().get(col);
            var hAlign = colDef.resolveHAlign(block.hAlign());
            var vAlign = colDef.resolveVAlign(block.vAlign());
            int cellW = columnWidths[col];

            if (cell.isIcon()) {
                int texW = colDef.textureWidth().orElse(16);
                int texH = colDef.textureHeight().orElse(16);

                int drawW = Math.max(1, (int) (texW * scale));
                int drawH = Math.max(1, (int) (texH * scale));

                int innerHeight = rowHeight - vPadding * 2;
                int innerWidth = cellW - hPadding * 2;

                int iconX = switch (hAlign) {
                    case LEFT -> offsetX + hPadding;
                    case CENTER -> offsetX + hPadding + Math.max(0, (innerWidth - drawW) / 2);
                    case RIGHT -> offsetX + cellW - hPadding - drawW;
                };

                int iconY = switch (vAlign) {
                    case TOP -> rowY + vPadding;
                    case MIDDLE -> rowY + vPadding + Math.max(0, (innerHeight - drawH) / 2);
                    case BOTTOM -> rowY + rowHeight - vPadding - drawH;
                };

                graphics.pose().pushPose();
                graphics.pose().translate(iconX, iconY, 0);
                graphics.pose().scale(scale, scale, 1.0f);
                graphics.blit(cell.icon(), 0, 0, 0, 0, texW, texH, texW, texH);
                graphics.pose().popPose();
                
            } else {

                int lineCount = cell.label().getLineCount();
                int textHeight = lineCount > 0
                        ? (int) Math.ceil(((lineCount - 1) * LINE_SPACING + GLYPH_HEIGHT) * scale)
                        : 0;
                int innerHeight = rowHeight - vPadding * 2;

                int textY = switch (vAlign) {
                    case TOP -> rowY + vPadding;
                    case MIDDLE -> rowY + vPadding + Math.max(0, (innerHeight - textHeight) / 2);
                    case BOTTOM -> rowY + rowHeight - vPadding - textHeight;
                };

                graphics.pose().pushPose();

                switch (hAlign) {
                    case LEFT -> {
                        graphics.pose().translate(offsetX + hPadding, textY, 0);
                        graphics.pose().scale(scale, scale, scale);
                        cell.label().renderLeftAligned(graphics, 0, 0, LINE_SPACING, cell.color());
                    }
                    case CENTER -> {
                        graphics.pose().translate(offsetX + cellW / 2f, textY, 0);
                        graphics.pose().scale(scale, scale, scale);
                        cell.label().renderCentered(graphics, 0, 0, LINE_SPACING, cell.color());
                    }
                    case RIGHT -> {
                        graphics.pose().translate(offsetX + cellW - hPadding, textY, 0);
                        graphics.pose().scale(scale, scale, scale);
                        cell.label().renderLeftAligned(graphics, -(cellW - hPadding * 2), 0, LINE_SPACING, cell.color());
                    }
                }

                graphics.pose().popPose();
            }

            offsetX += cellW;
        }
    }

    @Override
    protected void measure() {
        int columnCount = block.columns().size();
        if (columnCount == 0) {
            setRequiredHeight(0);
            return;
        }

        columnWidths = distributeWeightedWidths(getWidth(), block.columns());
        int hPadding = block.hPadding();
        int vPadding = block.vPadding();

        headerCells = new ArrayList<>(columnCount);
        headerHeight = 0;

        for (int col = 0; col < columnCount; col++) {
            var colDef = block.columns().get(col);
            int innerW = Math.max(1, (int) ((columnWidths[col] - hPadding * 2) / headerScale));

            Component text = block.headerCustomFont()
                    ? GuiToolkit.applyFont(Component.literal(colDef.header()))
                    : Component.literal(colDef.header());

            var label = MultiLineLabel.create(font, text, innerW);
            int lines = label.getLineCount();
            int cellH = lines > 0
                    ? (int) Math.ceil(((lines - 1) * LINE_SPACING + GLYPH_HEIGHT) * headerScale) + vPadding * 2
                    : vPadding * 2;
            cellH = Math.max(cellH, block.minRowHeight());

            headerCells.add(CellData.text(label, colDef.headerColor().orElse(0xFFFFFFFF)));
            headerHeight = Math.max(headerHeight, cellH);
        }

        contentCells = new ArrayList<>(block.rows().size());
        rowHeights = new int[block.rows().size()];

        for (int r = 0; r < block.rows().size(); r++) {
            var rawRow = block.rows().get(r);
            var rowCells = new ArrayList<CellData>(columnCount);
            int rowHeight = block.minRowHeight();

            for (int col = 0; col < columnCount; col++) {
                var colDef = block.columns().get(col);

                String raw = (col < rawRow.size() && !rawRow.get(col).isEmpty())
                        ? rawRow.get(col)
                        : block.emptyPlaceholder();

                int cellH;

                if (colDef.isIconColumn()) {
                    int texH = colDef.textureHeight().orElse(16);
                    cellH = (int) Math.ceil(texH * contentScale) + vPadding * 2;

                    ResourceLocation location = new ResourceLocation(raw);
                    rowCells.add(CellData.icon(location));
                } else {
                    int innerW = Math.max(1, (int) ((columnWidths[col] - hPadding * 2) / contentScale));

                    var label = MultiLineLabel.create(font, GuiToolkit.format(Component.literal(raw)), innerW);
                    int lines = label.getLineCount();
                    cellH = lines > 0
                            ? (int) Math.ceil(((lines - 1) * LINE_SPACING + GLYPH_HEIGHT) * contentScale) + vPadding * 2
                            : vPadding * 2;

                    rowCells.add(CellData.text(label, colDef.contentColor().orElse(Palette.LIGHT).decimal()));
                }

                rowHeight = Math.max(rowHeight, cellH);
            }

            rowHeights[r] = rowHeight;
            contentCells.add(rowCells);
        }

        setRequiredHeight(headerHeight + IntStream.of(rowHeights).sum());
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }

    private static int[] distributeWeightedWidths(int totalWidth, List<TableBlock.ColumnDef> columns) {
        int n = columns.size();
        float[] weights = new float[n];
        float weightSum = 0f;

        for (int i = 0; i < n; i++) {
            float w = columns.get(i).weight().orElse(1f);
            weights[i] = w;
            weightSum += w;
        }

        int[] widths = new int[n];
        int allocated = 0;
        for (int i = 0; i < n; i++) {
            widths[i] = (int) (totalWidth * weights[i] / weightSum);
            allocated += widths[i];
        }

        int remainder = totalWidth - allocated;
        for (int i = 0; i < remainder; i++) widths[i]++;

        return widths;
    }
}