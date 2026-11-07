package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Texture;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class LineEditControl extends Control {

    protected static final int PAD_X = 4;
    protected static final int PAD_Y = 3;

    protected final Font font = Minecraft.getInstance().font;

    @Getter protected String value = "";
    @Getter protected float scale = 1f;
    @Getter protected @Nullable Component placeholder = null;
    @Getter protected int maxLength = 256;
    @Getter protected @Nullable Predicate<String> filter = null;

    protected int cursorPos = 0;
    protected int selectionStart = -1;
    protected int selectionEnd = -1;
    protected int displayPos = 0;
    protected int cursorTick = 0;
    protected double lastScrollOffsetY = 0.0;

    protected @Nullable Consumer<String> onChanged;
    protected @Nullable Runnable onEnter;

    public LineEditControl() {
        super(0, 0, 80, 14, Component.empty());
        setSizeHints(SIZE_HINT_WIDTH);
    }

    public void setValue(String value) {
        this.value = clamp(value);
        this.cursorPos = this.value.length();
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.displayPos = 0;
        ensureCursorVisible();
        markDirty();
    }

    public void setScale(float scale) {
        this.scale = GuiToolkit.snapScale(scale);
        markDirty();
    }

    public void setPlaceholder(@Nullable Component placeholder) {
        this.placeholder = placeholder;
        markDirty();
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setFilter(@Nullable Predicate<String> filter) {
        this.filter = filter;
    }

    public void setOnChanged(@Nullable Consumer<String> onChanged) {
        this.onChanged = onChanged;
    }

    public void setOnEnter(@Nullable Runnable onEnter) {
        this.onEnter = onEnter;
    }

    public void selectAll() {
        selectionStart = 0;
        selectionEnd = value.length();
        cursorPos = selectionEnd;
        ensureCursorVisible();
    }

    public boolean hasSelection() {
        return selectionStart >= 0 && selectionEnd >= 0 && selectionStart != selectionEnd;
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        cursorTick++;
    }

    @Override
    protected void measure() {
        setMeasuredSize(getWidth(), (int) Math.ceil(10 * scale) + PAD_Y * 2);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (!context.isWithinScissor(this)) return;
        boolean hovered = active && context.isMouseOver(this, mouseX, mouseY);

        if (!active) context.pushAlpha(0.33f);
        GuiToolkit.draw(graphics, pickTexture(hovered), this);
        if (!active) context.popModulate();

        renderTextContent(graphics, context, mouseX, mouseY);
    }

    protected Texture pickTexture(boolean hovered) {
        return isFocused()
                ? Textures.buttonGray(true)
                : Textures.buttonGray(hovered);
    }

    protected void renderTextContent(GuiGraphics graphics, GuiContext context,
                                     int mouseX, int mouseY) {

        int innerW = innerWidth();

        graphics.pose().pushPose();
        graphics.pose().translate(getX() + PAD_X, getY() + PAD_Y, 0);
        graphics.pose().scale(scale, scale, scale);

        if (value.isEmpty() && placeholder != null) {
            String clipped = font.plainSubstrByWidth(placeholder.getString(), innerW);
            graphics.drawString(font, clipped, 0, 0, Palette.LIGHT.decimal(), false);
            if (isFocused() && (cursorTick / 10) % 2 == 0) {
                graphics.fill(0, -1, 1, 9, 0xFFFFFFFF);
            }
        } else {
            String visible = font.plainSubstrByWidth(value.substring(displayPos), innerW);
            int visEnd = displayPos + visible.length(); // exclusive

            if (hasSelection()) {
                int selS = Math.min(selectionStart, selectionEnd);
                int selE = Math.max(selectionStart, selectionEnd);

                int hS = Math.max(selS, displayPos);
                int hE = Math.min(selE, visEnd);

                if (hS < hE) {
                    int x0 = font.width(value.substring(displayPos, hS));
                    int x1 = font.width(value.substring(displayPos, hE));
                    graphics.fill(x0, -1, x1, 9, 0x66AADDFF);
                }
            }

            graphics.drawString(font, visible, 0, 0, 0xFFFFFF, false);

            if (isFocused() && cursorPos >= displayPos && cursorPos <= visEnd
                    && (cursorTick / 10) % 2 == 0) {
                int cx = font.width(value.substring(displayPos, cursorPos));
                graphics.fill(cx, -1, cx + 1, 9, 0xFFFFFFFF);
            }
        }

        graphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(GuiContext context, double mouseX, double mouseY, int button) {
        if (!isEffectivelyVisible() || !active) return false;
        boolean over = context.isMouseOver(this, mouseX, mouseY);
        if (over && button == 0) {
            setFocused(true);
            lastScrollOffsetY = context.scrollOffsetY();
            int pos = mouseToCharPos((int) mouseX, (int) mouseY);
            cursorPos = pos;
            selectionStart = pos;
            selectionEnd = pos;
            cursorTick = 0;
            return true;
        }
        if (!over && isFocused()) setFocused(false);
        return super.mouseClicked(context, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(GuiContext context, double mouseX, double mouseY,
                                int button, double dragX, double dragY) {
        if (!isFocused() || button != 0)
            return super.mouseDragged(context, mouseX, mouseY, button, dragX, dragY);
        lastScrollOffsetY = context.scrollOffsetY();
        int pos = mouseToCharPos((int) mouseX, (int) mouseY);
        cursorPos = pos;
        selectionEnd = pos;
        ensureCursorVisible();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) return false;
        boolean ctrl = isCmdOrCtrl(modifiers);
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;

        return switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> {
                moveCursorBy(ctrl ? wordLeft() : -1, shift);
                yield true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                moveCursorBy(ctrl ? wordRight() : 1, shift);
                yield true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                moveCursorTo(0, shift);
                yield true;
            }
            case GLFW.GLFW_KEY_END -> {
                moveCursorTo(value.length(), shift);
                yield true;
            }
            case GLFW.GLFW_KEY_BACKSPACE -> {
                deleteBackward(ctrl);
                yield true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                deleteForward(ctrl);
                yield true;
            }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                if (onEnter != null) onEnter.run();
                yield true;
            }
            case GLFW.GLFW_KEY_A -> {
                if (ctrl) {
                    selectAll();
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_KEY_C -> {
                if (ctrl && hasSelection()) {
                    copyToClipboard();
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_KEY_X -> {
                if (ctrl && hasSelection()) {
                    copyToClipboard();
                    deleteSelection();
                    yield true;
                }
                yield false;
            }
            case GLFW.GLFW_KEY_V -> {
                if (ctrl) {
                    pasteFromClipboard();
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (!isFocused() || c < 32) return false;
        insertText(String.valueOf(c));
        return true;
    }

    protected void insertText(String text) {
        if (hasSelection()) deleteSelection();
        int safePos = Math.max(0, Math.min(cursorPos, value.length()));
        cursorPos = safePos;
        String candidate = clamp(value.substring(0, safePos) + text + value.substring(safePos));
        int accepted = Math.max(0, candidate.length() - value.length());
        value = candidate;
        cursorPos += accepted;
        selectionStart = -1;
        selectionEnd = -1;
        ensureCursorVisible();
        if (onChanged != null) onChanged.accept(value);
    }

    private void deleteBackward(boolean word) {
        if (hasSelection()) {
            deleteSelection();
            return;
        }
        if (cursorPos == 0) return;
        int start = cursorPos + (word ? wordLeft() : -1);
        value = value.substring(0, start) + value.substring(cursorPos);
        cursorPos = start;
        ensureCursorVisible();
        if (onChanged != null) onChanged.accept(value);
    }

    private void deleteForward(boolean word) {
        if (hasSelection()) {
            deleteSelection();
            return;
        }
        if (cursorPos == value.length()) return;
        int end = cursorPos + (word ? wordRight() : 1);
        value = value.substring(0, cursorPos) + value.substring(end);
        ensureCursorVisible();
        if (onChanged != null) onChanged.accept(value);
    }

    protected void deleteSelection() {
        if (!hasSelection()) return;
        int s = Math.max(0, Math.min(selectionStart, selectionEnd));
        int e = Math.min(value.length(), Math.max(selectionStart, selectionEnd));
        if (s > e) {
            selectionStart = -1;
            selectionEnd = -1;
            return;
        }
        value = value.substring(0, s) + value.substring(e);
        cursorPos = s;
        selectionStart = -1;
        selectionEnd = -1;
        ensureCursorVisible();
        if (onChanged != null) onChanged.accept(value);
    }

    private void copyToClipboard() {
        if (!hasSelection()) return;
        int s = Math.min(selectionStart, selectionEnd);
        int e = Math.max(selectionStart, selectionEnd);
        Minecraft.getInstance().keyboardHandler.setClipboard(value.substring(s, e));
    }

    private void pasteFromClipboard() {
        String clip = Minecraft.getInstance().keyboardHandler.getClipboard();
        if (!clip.isEmpty()) insertText(clip);
    }

    protected void moveCursorBy(int delta, boolean shift) {
        if (!shift && hasSelection()) {
            int s = Math.min(selectionStart, selectionEnd);
            int e = Math.max(selectionStart, selectionEnd);
            cursorPos = delta < 0 ? s : e;
            selectionStart = -1;
            selectionEnd = -1;
            ensureCursorVisible();
            cursorTick = 0;
        } else {
            moveCursorTo(cursorPos + delta, shift);
        }
    }

    protected void moveCursorTo(int pos, boolean shift) {
        int clamped = Math.max(0, Math.min(value.length(), pos));
        if (shift) {
            if (selectionStart < 0) selectionStart = cursorPos;
            selectionEnd = clamped;
        } else {
            selectionStart = -1;
            selectionEnd = -1;
        }
        cursorPos = clamped;
        ensureCursorVisible();
        cursorTick = 0;
    }

    private int wordLeft() {
        if (cursorPos == 0) return 0;
        int pos = cursorPos - 1;
        while (pos > 0 && !Character.isLetterOrDigit(value.charAt(pos - 1))) pos--;
        while (pos > 0 && Character.isLetterOrDigit(value.charAt(pos - 1))) pos--;
        return pos - cursorPos;
    }

    private int wordRight() {
        if (cursorPos == value.length()) return 0;
        int pos = cursorPos;
        while (pos < value.length() && !Character.isLetterOrDigit(value.charAt(pos))) pos++;
        while (pos < value.length() && Character.isLetterOrDigit(value.charAt(pos))) pos++;
        return pos - cursorPos;
    }

    protected void ensureCursorVisible() {
        int innerW = innerWidth();

        if (cursorPos < displayPos) {
            displayPos = cursorPos;
        }

        while (font.width(value.substring(displayPos, cursorPos)) > innerW) {
            displayPos++;
        }

        if (displayPos > 0) {
            while (displayPos > 0
                    && font.width(value.substring(displayPos - 1)) <= innerW) {
                displayPos--;
            }
        }
    }

    protected int mouseToCharPos(int mouseX, int mouseY) {
        cursorPos = Math.max(0, Math.min(cursorPos, value.length()));
        displayPos = Math.max(0, Math.min(displayPos, value.length()));
        float localX = (mouseX - (getX() + PAD_X)) / scale;
        if (localX <= 0) return displayPos;

        String tail = value.substring(displayPos);
        for (int i = 1; i <= tail.length(); i++) {
            if (font.width(tail.substring(0, i)) > localX) {
                return displayPos + i - 1;
            }
        }
        return displayPos + tail.length();
    }

    protected final void drawLineSelection(GuiGraphics graphics,
                                           String lineText, int lineX, int lineY,
                                           int lineDispPos) {
        if (!hasSelection()) return;
        int selS = Math.min(selectionStart, selectionEnd);
        int selE = Math.max(selectionStart, selectionEnd);
        int lineEnd = lineDispPos + lineText.length();

        int hS = Math.max(selS, lineDispPos);
        int hE = Math.min(selE, lineEnd);
        if (hS >= hE) return;

        int x0 = lineX + font.width(lineText.substring(0, hS - lineDispPos));
        int x1 = lineX + font.width(lineText.substring(0, hE - lineDispPos));
        graphics.fill(x0, lineY - 1, x1, lineY + 9, 0x66AADDFF);
    }

    protected final void drawLineCursor(GuiGraphics graphics,
                                        String lineText, int lineX, int lineY,
                                        int lineDispPos) {
        if (!isFocused() || (cursorTick / 10) % 2 != 0) return;
        int lineEnd = lineDispPos + lineText.length();
        if (cursorPos < lineDispPos || cursorPos > lineEnd) return;
        int col = cursorPos - lineDispPos;
        int cx = lineX + font.width(lineText.substring(0, col));
        graphics.fill(cx, lineY - 1, cx + 1, lineY + 9, 0xFFFFFFFF);
    }

    protected static boolean isCmdOrCtrl(int modifiers) {
        int mod = Minecraft.ON_OSX ? GLFW.GLFW_MOD_SUPER : GLFW.GLFW_MOD_CONTROL;
        return (modifiers & mod) != 0;
    }

    protected int innerWidth() {
        return (int) ((getWidth() - PAD_X * 2) / scale);
    }

    private String clamp(String text) {
        if (text.length() > maxLength) text = text.substring(0, maxLength);
        if (filter != null && !filter.test(text)) return value;
        return text;
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }
}