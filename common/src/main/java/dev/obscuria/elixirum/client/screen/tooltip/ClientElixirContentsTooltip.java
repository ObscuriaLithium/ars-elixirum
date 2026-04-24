package dev.obscuria.elixirum.client.screen.tooltip;

import dev.obscuria.elixirum.client.screen.tooltip.components.EffectListComponent;
import dev.obscuria.elixirum.common.world.tooltip.ElixirContentsTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public final class ClientElixirContentsTooltip implements ClientTooltipComponent {

    private static final int SEPARATION = 2;
    private static final int BOTTOM_MARGIN = 3;

    //private final TraitTableComponent traitTable;
    private final EffectListComponent effectList;

    public ClientElixirContentsTooltip(ElixirContentsTooltip tooltip) {
        //this.traitTable = new TraitTableComponent(120, tooltip.contents());
        this.effectList = new EffectListComponent(tooltip.contents());
    }

    @Override
    public int getHeight() {
        return effectList.getHeight() + BOTTOM_MARGIN;
        //return traitTable.getHeight() + SEPARATION + effectList.getHeight() + BOTTOM_MARGIN;
    }

    @Override
    public int getWidth(Font font) {
        return effectList.getWidth(font);
        //return Math.max(traitTable.getWidth(font), effectList.getWidth(font));
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource source) {
        //this.traitTable.renderText(font, x, y, matrix, source);
        this.effectList.renderText(font, x, y, matrix, source);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        //this.traitTable.renderImage(font, x, y, guiGraphics);
        this.effectList.renderImage(font, x, y, guiGraphics);
    }
}
