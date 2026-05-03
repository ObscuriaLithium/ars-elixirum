package dev.obscuria.elixirum.client.screen.tooltips;

import dev.obscuria.elixirum.client.screen.tooltips.components.EffectListComponent;
import dev.obscuria.elixirum.common.world.tooltip.ElixirContentsTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public record ClientElixirContentsTooltip(
        EffectListComponent effectList
) implements ClientTooltipComponent {

    private static final int BOTTOM_MARGIN = 3;

    public ClientElixirContentsTooltip(ElixirContentsTooltip effectList) {
        this(new EffectListComponent(effectList.contents()));
    }

    @Override
    public int getHeight() {
        return effectList.getHeight() + BOTTOM_MARGIN;
    }

    @Override
    public int getWidth(Font font) {
        return effectList.getWidth(font);
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource source) {
        this.effectList.renderText(font, x, y, matrix, source);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        this.effectList.renderImage(font, x, y, guiGraphics);
    }
}
