package dev.obscuria.elixirum.client.screen.alchemy.controls;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public abstract class ElixirControl extends Control {

    public final CachedElixir elixir;

    protected final Selection<CachedElixir> selection;
    protected float highlight = 0f;
    protected float highlightO = 0f;

    protected ElixirControl(Selection<CachedElixir> selection, CachedElixir elixir) {
        super(0, 0, 15, 19, CommonComponents.EMPTY);
        this.elixir = elixir;
        this.selection = selection;
    }

    public CachedElixir elixir() {
        return elixir;
    }

    public void makeClickHandler(Consumer<CachedElixir> action) {
        this.clickHandler = ClickHandler.leftClick(ElixirControl.class, it -> {
            action.accept(it.elixir());
            return true;
        });
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (!context.isVisible(this)) return;
        this.isHovered = context.isMouseOver(this, mouseX, mouseY);
        if (isHoveredOrFocused()) this.highlight = 1f;
        renderElixir(graphics, context, mouseX, mouseY);
        if (this.isSelected()) GuiToolkit.draw(graphics, Textures.OUTLINE_WHITE, this);
    }

    @Override
    public void tick() {
        this.highlightO = highlight;
        if (isHoveredOrFocused() || highlight <= 0) return;
        this.highlight -= 0.1f;
    }

    protected void renderElixir(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        final var highlight = lerpHighlight();
        if (highlight > 0.001f) {
            graphics.pose().pushPose();
            graphics.pose().translate(getX() + getWidth() * 0.5f, getY() + getHeight() * 0.5f, 0f);
            graphics.pose().scale(highlight, highlight, highlight);
            graphics.pose().translate(getWidth() * -0.5f, getHeight() * -0.5f, 0f);
            GuiToolkit.draw(graphics, Textures.SOLID_PURPLE, 0, 0, getWidth(), getHeight());
            graphics.pose().popPose();
        }
        graphics.renderItem(elixir.stack().get(), getX() - 1, getY() + 1);
    }

    protected boolean isSelected() {
        return selection.get() == elixir;
    }

    @Override
    protected void measure() {
        setMeasuredSize(15, 19);
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }

    private float lerpHighlight() {
        final var delta = Minecraft.getInstance().getFrameTime();
        return (float) Math.pow(Mth.lerp(delta, highlightO, highlight), 2.0);
    }
}
