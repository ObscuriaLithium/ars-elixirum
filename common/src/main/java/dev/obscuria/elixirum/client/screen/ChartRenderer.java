package dev.obscuria.elixirum.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public final class ChartRenderer {

    public static void drawChart(
            GuiGraphics graphics,
            int x,
            int y,
            int size,
            List<ChartElement> elements
    ) {
        final var pose = graphics.pose();
        final var matrix = pose.last().pose();
        final var totalWeight = (float) elements.stream().mapToDouble(ChartElement::weight).sum();

        final var centerX = x + size / 2f;
        final var centerY = y + size / 2f;

        final var angleStep = 360f / 64f;
        var currentAngle = -90f - angleStep / 2f;

        for (final var element : elements) {

            final var sectorSize = (element.weight() / totalWeight) * 360f;

            final var r = element.color().red();
            final var g = element.color().green();
            final var b = element.color().blue();

            var angle = currentAngle;
            var isFirstSegment = true;

            while (angle < currentAngle + sectorSize) {
                float nextAngle = Math.min(angle + angleStep, currentAngle + sectorSize);
                float alpha = isFirstSegment ? 0f : 1f;

                float x1 = centerX + size * 0.5f * (float) Math.cos(Math.toRadians(nextAngle));
                float y1 = centerY + size * 0.5f * (float) Math.sin(Math.toRadians(nextAngle));

                float x2 = centerX + size * 0.5f * (float) Math.cos(Math.toRadians(angle));
                float y2 = centerY + size * 0.5f * (float) Math.sin(Math.toRadians(angle));

                float x3 = centerX + size * 0.4f * (float) Math.cos(Math.toRadians(angle));
                float y3 = centerY + size * 0.4f * (float) Math.sin(Math.toRadians(angle));

                float x4 = centerX + size * 0.4f * (float) Math.cos(Math.toRadians(nextAngle));
                float y4 = centerY + size * 0.4f * (float) Math.sin(Math.toRadians(nextAngle));

                graphics.drawManaged(() -> {
                    final var consumer = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    consumer.vertex(matrix, x1, y1, 0f).color(r, g, b, alpha).endVertex();
                    consumer.vertex(matrix, x2, y2, 0f).color(r, g, b, alpha).endVertex();
                    consumer.vertex(matrix, x3, y3, 0f).color(r, g, b, alpha).endVertex();
                    consumer.vertex(matrix, x4, y4, 0f).color(r, g, b, alpha).endVertex();
                });

                angle = nextAngle;
                isFirstSegment = false;
            }

            currentAngle += sectorSize;
        }
    }
}
