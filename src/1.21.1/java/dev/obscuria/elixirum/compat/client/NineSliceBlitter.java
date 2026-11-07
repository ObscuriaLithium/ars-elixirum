package dev.obscuria.elixirum.compat.client;

import com.mojang.math.Divisor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class NineSliceBlitter {

    private NineSliceBlitter() {}

    public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
                            int border, int sourceWidth, int sourceHeight, int u, int v) {
        int bw = Math.min(border, width / 2);
        int bh = Math.min(border, height / 2);
        int innerWidth = width - 2 * bw;
        int innerHeight = height - 2 * bh;
        int sourceInnerWidth = sourceWidth - 2 * bw;
        int sourceInnerHeight = sourceHeight - 2 * bh;
        repeat(graphics, texture, x, y, bw, bh, u, v, bw, bh);
        repeat(graphics, texture, x + bw, y, innerWidth, bh, u + bw, v, sourceInnerWidth, bh);
        repeat(graphics, texture, x + width - bw, y, bw, bh, u + sourceWidth - bw, v, bw, bh);
        repeat(graphics, texture, x, y + bh, bw, innerHeight, u, v + bh, bw, sourceInnerHeight);
        repeat(graphics, texture, x + bw, y + bh, innerWidth, innerHeight, u + bw, v + bh, sourceInnerWidth, sourceInnerHeight);
        repeat(graphics, texture, x + width - bw, y + bh, bw, innerHeight, u + sourceWidth - bw, v + bh, bw, sourceInnerHeight);
        repeat(graphics, texture, x, y + height - bh, bw, bh, u, v + sourceHeight - bh, bw, bh);
        repeat(graphics, texture, x + bw, y + height - bh, innerWidth, bh, u + bw, v + sourceHeight - bh, sourceInnerWidth, bh);
        repeat(graphics, texture, x + width - bw, y + height - bh, bw, bh, u + sourceWidth - bw, v + sourceHeight - bh, bw, bh);
    }

    private static void repeat(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height,
                               int u, int v, int sourceWidth, int sourceHeight) {
        if (width <= 0 || height <= 0) return;
        // Match 1.20's evenly divided, centered texture tiles instead of stretching the panel edges.
        var columns = new Divisor(width, Mth.positiveCeilDiv(width, sourceWidth));
        while (columns.hasNext()) {
            int w = columns.nextInt();
            int tileY = y;
            var rows = new Divisor(height, Mth.positiveCeilDiv(height, sourceHeight));
            while (rows.hasNext()) {
                int h = rows.nextInt();
                graphics.blit(texture, x, tileY, u + (sourceWidth - w) / 2, v + (sourceHeight - h) / 2, w, h);
                tileY += h;
            }
            x += w;
        }
    }
}
