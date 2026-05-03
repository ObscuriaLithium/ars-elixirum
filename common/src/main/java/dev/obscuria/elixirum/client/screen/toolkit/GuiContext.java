package dev.obscuria.elixirum.client.screen.toolkit;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.fragmentum.util.color.ARGB;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayDeque;

public final class GuiContext {

    private static final ArrayDeque<GuiContext> POOL = new ArrayDeque<>(8);
    private static final int MAX_DEPTH = 16;

    private final int[] stackX1 = new int[MAX_DEPTH];
    private final int[] stackY1 = new int[MAX_DEPTH];
    private final int[] stackX2 = new int[MAX_DEPTH];
    private final int[] stackY2 = new int[MAX_DEPTH];
    private final boolean[] stackApplied = new boolean[MAX_DEPTH];
    private final float[] stackModR = new float[MAX_DEPTH];
    private final float[] stackModG = new float[MAX_DEPTH];
    private final float[] stackModB = new float[MAX_DEPTH];
    private final float[] stackModA = new float[MAX_DEPTH];
    private final double[] stackScrollY = new double[MAX_DEPTH];

    private int scissorX1, scissorY1, scissorX2, scissorY2;
    private int scissorDepth;
    private float modR, modG, modB, modA;
    private int modDepth;

    private double scrollOffsetY = 0.0;
    private int scrollDepth = 0;

    public static GuiContext forRoot(Control root) {
        var context = POOL.isEmpty() ? new GuiContext() : POOL.pop();

        context.scissorX1 = root.getX();
        context.scissorY1 = root.getY();
        context.scissorX2 = root.getX() + root.getWidth();
        context.scissorY2 = root.getY() + root.getHeight();
        context.scissorDepth = 0;

        context.modR = 1f;
        context.modG = 1f;
        context.modB = 1f;
        context.modA = 1f;
        context.modDepth = 0;

        context.scrollOffsetY = 0.0;
        context.scrollDepth = 0;

        return context;
    }

    public void pushScissor(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        stackX1[scissorDepth] = scissorX1;
        stackY1[scissorDepth] = scissorY1;
        stackX2[scissorDepth] = scissorX2;
        stackY2[scissorDepth] = scissorY2;

        scissorX1 = Math.max(scissorX1, x1);
        scissorY1 = Math.max(scissorY1, y1);
        scissorX2 = Math.min(scissorX2, x2);
        scissorY2 = Math.min(scissorY2, y2);

        stackApplied[scissorDepth] = applyScissor(graphics);
        scissorDepth++;
    }

    public void popScissor(GuiGraphics graphics) {
        if (scissorDepth <= 0) return;
        scissorDepth--;

        if (stackApplied[scissorDepth]) graphics.disableScissor();

        scissorX1 = stackX1[scissorDepth];
        scissorY1 = stackY1[scissorDepth];
        scissorX2 = stackX2[scissorDepth];
        scissorY2 = stackY2[scissorDepth];

        if (scissorDepth > 0 && stackApplied[scissorDepth - 1]) applyScissor(graphics);
    }

    private boolean applyScissor(GuiGraphics graphics) {
        if (scissorX2 <= scissorX1 || scissorY2 <= scissorY1) return false;
        graphics.enableScissor(scissorX1, scissorY1, scissorX2, scissorY2);
        return true;
    }

    public void pushScrollOffset(double scrollY) {
        stackScrollY[scrollDepth++] = scrollOffsetY;
        scrollOffsetY += scrollY;
    }

    public void popScrollOffset() {
        if (scrollDepth > 0) scrollOffsetY = stackScrollY[--scrollDepth];
    }

    public void pushModulate(RGB color) {
        pushModulate(color.red(), color.green(), color.blue(), 1f);
    }

    public void pushModulate(ARGB color) {
        pushModulate(color, color.alpha());
    }

    public void pushModulate(ARGB color, float alpha) {
        pushModulate(color.red(), color.green(), color.blue(), alpha);
    }

    public void pushModulate(float r, float g, float b, float a) {
        stackModR[modDepth] = modR;
        stackModG[modDepth] = modG;
        stackModB[modDepth] = modB;
        stackModA[modDepth] = modA;
        modDepth++;

        modR *= r;
        modG *= g;
        modB *= b;
        modA *= a;

        applyModulate();
    }

    public void pushAlpha(float alpha) {
        pushModulate(1f, 1f, 1f, alpha);
    }

    public void popModulate() {
        if (modDepth > 0) {
            modDepth--;
            modR = stackModR[modDepth];
            modG = stackModG[modDepth];
            modB = stackModB[modDepth];
            modA = stackModA[modDepth];
            applyModulate();
        }
    }

    private void applyModulate() {
        RenderSystem.setShaderColor(modR, modG, modB, modA);
    }

    public boolean isEffectivelyOpaque() {
        return modA > 0.004f;
    }

    public boolean isMouseOver(Control control, double mouseX, double mouseY) {
        var controlX = control.getX();
        var controlY = control.getY() - scrollOffsetY;
        if (mouseX < controlX || mouseX >= controlX + control.getWidth()) return false;
        if (mouseY < controlY || mouseY >= controlY + control.getHeight()) return false;
        return mouseX >= scissorX1 && mouseX < scissorX2 && mouseY >= scissorY1 && mouseY < scissorY2;
    }

    public boolean isWithinScissor(Control node) {
        var adjustedY = node.getY() - scrollOffsetY;
        return node.getX() < scissorX2
                && node.getX() + node.getWidth() > scissorX1
                && adjustedY < scissorY2
                && adjustedY + node.getHeight() > scissorY1;
    }

    public boolean isVisible(Control control) {
        return isEffectivelyOpaque()
                && control.isEffectivelyVisible()
                && isWithinScissor(control);
    }

    public double scrollOffsetY() {
        return scrollOffsetY;
    }

    public void release() {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        POOL.push(this);
    }

    private GuiContext() {}
}