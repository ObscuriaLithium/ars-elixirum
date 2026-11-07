package dev.obscuria.elixirum.client.screen.tooltips;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.tooltips.components.CompositionChartComponent;
import dev.obscuria.elixirum.client.screen.tooltips.components.TraitTableComponent;
import dev.obscuria.elixirum.common.world.tooltip.AlchemyPropertiesTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

public final class ClientAlchemyPropertiesTooltip implements ClientTooltipComponent {

    private static final int PADDING = 3;
    private static final int SEPARATION = 5;
    private static final int MARGIN_BOTTOM = 3;

    private final CompositionChartComponent chart;
    private final TraitTableComponent traits;

    public ClientAlchemyPropertiesTooltip(AlchemyPropertiesTooltip tooltip) {
        this.chart = new CompositionChartComponent(tooltip.stack(), tooltip.properties());
        var chartWidth = chart.getWidth(Minecraft.getInstance().font);
        this.traits = new TraitTableComponent(Math.max(120, chartWidth),
                tooltip.stack().getItem(),
                tooltip.properties());
    }

    @Override
    public int getHeight() {
        return PADDING * 2 + chart.getHeight() + SEPARATION + traits.getHeight() + MARGIN_BOTTOM;
    }

    @Override
    public int getWidth(Font font) {
        return PADDING * 2 + Math.max(chart.getWidth(font), traits.getWidth(font));
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource source) {
        chart.renderText(font, x + PADDING, y + PADDING, matrix, source);
        traits.renderText(font, x + PADDING, y + PADDING + chart.getHeight() + SEPARATION, matrix, source);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        var width = getWidth(font);
        var height = getHeight() - MARGIN_BOTTOM;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 0.33f);
        GuiToolkit.draw(graphics, Textures.buttonGray(false), x, y, width, height);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        chart.renderImage(font, x + PADDING, y + PADDING, graphics);
        traits.renderImage(font, x + PADDING, y + PADDING + chart.getHeight() + SEPARATION, graphics);
    }
}
