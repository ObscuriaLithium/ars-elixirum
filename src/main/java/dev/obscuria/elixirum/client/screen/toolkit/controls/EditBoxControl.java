package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Texture;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class EditBoxControl extends LineEditControl {

    private static final int LINE_H = 10;
    private int displayLineOffset = 0;

    public EditBoxControl() {
        setSizeHints(SIZE_HINT_WIDTH | SIZE_HINT_HEIGHT);
    }

    @Override
    protected void measure() {
        int minH = (int) Math.ceil(LINE_H * scale) * 3 + PAD_Y * 2;
        setMeasuredSize(getWidth(), Math.max(getHeight(), minH));
    }

    @Override
    protected Texture pickTexture(boolean hovered) {
        return isFocused()
                ? Textures.buttonGray(true)
                : Textures.buttonGray(hovered);
    }

    @Override
    protected void renderTextContent(GuiGraphics graphics, GuiContext context,
                                     int mouseX, int mouseY) {
        int innerW = innerWidth();
        int visLines = visibleLineCount();

        graphics.pose().pushPose();
        graphics.pose().translate(getX() + PAD_X, getY() + PAD_Y, 0);
        graphics.pose().scale(scale, scale, scale);

        if (value.isEmpty() && placeholder != null) {
            String clipped = font.plainSubstrByWidth(placeholder.getString(), innerW);
            graphics.drawString(font, clipped, 0, 0, 0xAAAAAA, false);
            if (isFocused() && (cursorTick / 10) % 2 == 0) {
                graphics.fill(0, -1, 1, 9, 0xFFFFFFFF);
            }
        } else {
            List<VisualLine> vlines = buildVisualLines(innerW);
            int clampedOffset = Math.max(0, Math.min(displayLineOffset, Math.max(0, vlines.size() - 1)));
            if (clampedOffset != displayLineOffset) displayLineOffset = clampedOffset;

            for (int vi = displayLineOffset;
                 vi < vlines.size() && (vi - displayLineOffset) < visLines;
                 vi++) {

                VisualLine vl = vlines.get(vi);
                int renderY = (vi - displayLineOffset) * LINE_H;

                drawLineSelection(graphics, vl.text, 0, renderY, vl.startAbs);
                graphics.drawString(font, vl.text, 0, renderY, 0xFFFFFF, false);
                drawLineCursor(graphics, vl.text, 0, renderY, vl.startAbs);
            }
        }

        graphics.pose().popPose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) return false;
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (onEnter == null || shift) {
                insertText("\n");
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_UP) {
            moveCursorVertical(-1, shift);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            moveCursorVertical(1, shift);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void moveCursorVertical(int lineDelta, boolean shift) {
        int innerW = innerWidth();
        List<VisualLine> vlines = buildVisualLines(innerW);
        int curVi = visualLineIndexForPos(vlines, cursorPos);
        int target = Math.max(0, Math.min(vlines.size() - 1, curVi + lineDelta));
        if (target == curVi) return;

        VisualLine curVl = vlines.get(curVi);
        int colInVl = cursorPos - curVl.startAbs;
        int pixelCol = font.width(curVl.text.substring(0, Math.min(colInVl, curVl.text.length())));

        VisualLine tgtVl = vlines.get(target);
        int newAbs = tgtVl.startAbs + charIndexForPixel(tgtVl.text, pixelCol);

        if (shift) {
            if (selectionStart < 0) selectionStart = cursorPos;
            selectionEnd = newAbs;
        } else {
            selectionStart = -1;
            selectionEnd = -1;
        }
        cursorPos = newAbs;
        cursorTick = 0;
        ensureCursorVisible();
    }

    @Override
    protected void ensureCursorVisible() {
        displayPos = 0;

        int innerW = innerWidth();
        List<VisualLine> vlines = buildVisualLines(innerW);
        int curVi = visualLineIndexForPos(vlines, cursorPos);
        int visLines = visibleLineCount();

        if (curVi < displayLineOffset) {
            displayLineOffset = curVi;
        } else if (curVi >= displayLineOffset + visLines) {
            displayLineOffset = curVi - visLines + 1;
        }
    }

    @Override
    protected int mouseToCharPos(int mouseX, int mouseY) {
        int innerW = innerWidth();

        float localX = (mouseX - (getX() + PAD_X)) / scale;
        float localY = (float) ((mouseY - (getY() + PAD_Y - lastScrollOffsetY)) / scale);

        cursorPos = Math.max(0, Math.min(cursorPos, value.length()));
        displayPos = 0;

        List<VisualLine> vlines = buildVisualLines(innerW);
        int clickedVi = Math.max(0, Math.min(
                vlines.size() - 1,
                displayLineOffset + (int) (localY / LINE_H)));

        VisualLine vl = vlines.get(clickedVi);
        if (localX <= 0) return vl.startAbs;
        return vl.startAbs + charIndexForPixel(vl.text, (int) localX);
    }

    private List<VisualLine> buildVisualLines(int innerW) {
        List<VisualLine> result = new ArrayList<>();
        if (value.isEmpty()) {
            result.add(new VisualLine("", 0));
            return result;
        }

        String[] logical = value.split("\n", -1);
        int abs = 0;

        for (String logLine : logical) {
            if (logLine.isEmpty()) {
                result.add(new VisualLine("", abs));
            } else {
                int lineStart = 0;
                while (lineStart < logLine.length()) {
                    String remaining = logLine.substring(lineStart);
                    String fits = font.plainSubstrByWidth(remaining, innerW);
                    if (fits.isEmpty()) {
                        fits = remaining.substring(0, 1);
                    }

                    int segLen = fits.length();
                    if (lineStart + segLen < logLine.length()) {
                        int lastSpace = fits.lastIndexOf(' ');
                        if (lastSpace > 0) {
                            segLen = lastSpace + 1;
                        }
                    }

                    result.add(new VisualLine(logLine.substring(lineStart, lineStart + segLen),
                            abs + lineStart));
                    lineStart += segLen;
                }
            }
            abs += logLine.length() + 1;
        }

        return result;
    }

    private static int visualLineIndexForPos(List<VisualLine> vlines, int pos) {
        for (int i = vlines.size() - 1; i >= 0; i--) {
            if (pos >= vlines.get(i).startAbs) return i;
        }
        return 0;
    }

    private int charIndexForPixel(String text, int pixelX) {
        for (int i = 1; i <= text.length(); i++) {
            if (font.width(text.substring(0, i)) > pixelX) return i - 1;
        }
        return text.length();
    }

    private record VisualLine(String text, int startAbs) {}

    private int visibleLineCount() {
        int innerH = (int) ((getHeight() - PAD_Y * 2) / scale);
        return Math.max(1, innerH / LINE_H);
    }
}
