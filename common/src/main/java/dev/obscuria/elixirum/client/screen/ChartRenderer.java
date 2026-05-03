package dev.obscuria.elixirum.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public final class ChartRenderer {

    public static void renderChart(
            GuiGraphics graphics,
            int x,
            int y,
            int size,
            List<ChartElement> elements
    ) {
        final var matrix = graphics.pose().last().pose();
        final var totalWeight = (float) elements.stream().mapToDouble(ChartElement::weight).sum();

        final float cx = x + size / 2f;
        final float cy = y + size / 2f;
        final float outerR = size * 0.5f;
        final float innerR = size * 0.35f;
        final float outerGlow = outerR - 1.5f;

        final float angleStep = 360f / 64f;
        float currentAngle = -90f - angleStep / 2f;

        for (final var element : elements) {
            final float sectorSize = (element.weight() / totalWeight) * 360f;

            final float r = element.color().red();
            final float g = element.color().green();
            final float b = element.color().blue();

            final float alphaOuter = 1.00f;
            final float alphaInner = 0.3f;

            float angle = currentAngle;
            boolean isFirst = true;

            while (angle < currentAngle + sectorSize) {
                float next = Math.min(angle + angleStep, currentAngle + sectorSize);
                float aOuter = isFirst ? 0f : alphaOuter;
                float aInner = isFirst ? 0f : alphaInner;

                float ox1 = cx + outerR * (float) Math.cos(Math.toRadians(next));
                float oy1 = cy + outerR * (float) Math.sin(Math.toRadians(next));
                float ox2 = cx + outerR * (float) Math.cos(Math.toRadians(angle));
                float oy2 = cy + outerR * (float) Math.sin(Math.toRadians(angle));

                float ix1 = cx + innerR * (float) Math.cos(Math.toRadians(angle));
                float iy1 = cy + innerR * (float) Math.sin(Math.toRadians(angle));
                float ix2 = cx + innerR * (float) Math.cos(Math.toRadians(next));
                float iy2 = cy + innerR * (float) Math.sin(Math.toRadians(next));

                final float fao = aOuter, fai = aInner;

                graphics.drawManaged(() -> {
                    final var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    buf.vertex(matrix, ox1, oy1, 0f).color(r, g, b, fao).endVertex();
                    buf.vertex(matrix, ox2, oy2, 0f).color(r, g, b, fao).endVertex();
                    buf.vertex(matrix, ix1, iy1, 0f).color(r, g, b, fai).endVertex();
                    buf.vertex(matrix, ix2, iy2, 0f).color(r, g, b, fai).endVertex();
                });

                if (!isFirst) {
                    float hox1 = cx + outerGlow * (float) Math.cos(Math.toRadians(next));
                    float hoy1 = cy + outerGlow * (float) Math.sin(Math.toRadians(next));
                    float hox2 = cx + outerGlow * (float) Math.cos(Math.toRadians(angle));
                    float hoy2 = cy + outerGlow * (float) Math.sin(Math.toRadians(angle));

                    graphics.drawManaged(() -> {
                        final var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                        buf.vertex(matrix, ox1, oy1, 0f).color(r, g, b, 1.0f).endVertex();
                        buf.vertex(matrix, ox2, oy2, 0f).color(r, g, b, 1.0f).endVertex();
                        buf.vertex(matrix, hox2, hoy2, 0f).color(r, g, b, fao).endVertex();
                        buf.vertex(matrix, hox1, hoy1, 0f).color(r, g, b, fao).endVertex();
                    });
                }

                angle = next;
                isFirst = false;
            }

            currentAngle += sectorSize;
        }
    }
}