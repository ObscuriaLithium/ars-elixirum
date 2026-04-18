package dev.obscuria.elixirum.client.screen.widgets;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixirProvider;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

public abstract class AbstractElixirWidget
        extends HierarchicalControl
        implements CachedElixirProvider {

    public final CachedElixir elixir;
    private final SelectionState<CachedElixir> selection;
    private float highlight = 0f;
    private float highlightO = 0f;

    protected AbstractElixirWidget(SelectionState<CachedElixir> selection, CachedElixir elixir) {
        super(0, 0, 15, 19, CommonComponents.EMPTY);
        this.elixir = elixir;
        this.selection = selection;
    }

    @Override
    public CachedElixir elixir() {
        return elixir;
    }

    public void makeStackClickAction(Consumer<CachedElixir> action) {
        this.clickAction = ClickAction.<AbstractElixirWidget>leftClick(it -> action.accept(it.elixir()));
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (!transform.isWithinScissor()) {
            this.highlight = 0f;
            return;
        }
        if (transform.isMouseOver(mouseX, mouseY)) this.highlight = 1f;
        final var highlight = lerpHighlight();
        if (highlight > 0.001f) {
            graphics.pose().pushPose();
            graphics.pose().translate(rect.centerX(), rect.centerY(), 0f);
            graphics.pose().scale(highlight, highlight, highlight);
            graphics.pose().translate(rect.width() / -2f, rect.height() / -2f, 0f);
            GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.SOLID_PURPLE, 0, 0, rect.width(), rect.height());
            graphics.pose().popPose();
        }
        graphics.renderItem(elixir.stack().get(), rect.x() - 1, rect.y() + 1);
        if (this.isSelected()) GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_WHITE, this);
    }

    @Override
    public void tick() {
        this.highlightO = highlight;
        if (isHovered || highlight <= 0) return;
        this.highlight -= 0.1f;
    }

    @Override
    public void reorganize() {}

    protected boolean isSelected() {
        return selection.get() == elixir;
    }

    @Override
    protected boolean hasContents() {
        return true;
    }

    private float lerpHighlight() {
        final var delta = Minecraft.getInstance().getFrameTime();
        return (float) Math.pow(Mth.lerp(delta, highlightO, highlight), 2.0);
    }
}
