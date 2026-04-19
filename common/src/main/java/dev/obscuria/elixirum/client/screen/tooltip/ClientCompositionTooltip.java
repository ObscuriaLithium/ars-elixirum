package dev.obscuria.elixirum.client.screen.tooltip;

import dev.obscuria.elixirum.client.screen.ChartElement;
import dev.obscuria.elixirum.client.screen.ChartRenderer;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceProvider;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.world.tooltip.CompositionTooltip;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ClientCompositionTooltip(
        Aspect aspect,
        List<ChartElement> elements,
        List<LineInfo> lines
) implements ClientTooltipComponent {

    public static ClientTooltipComponent of(CompositionTooltip tooltip) {
        return new ClientCompositionTooltip(
                tooltip.aspect(),
                buildChart(tooltip.stack(), tooltip.provider()),
                buildLines(tooltip.stack(), tooltip.provider()));
    }

    @Override
    public int getHeight() {
        return Math.max(32, lines.stream().mapToInt(LineInfo::getHeight).sum()) + 3;
    }

    @Override
    public int getWidth(Font font) {
        return 32 + lines.stream().mapToInt(it -> it.getWidth(font)).max().orElse(0);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        ChartRenderer.drawChart(graphics, x, y, 32, elements);
        graphics.blit(aspect.texture, x + 7, y + 7, 0, 0, 18, 18, 18, 18);
        for (var i = 0; i < lines.size(); i++) {
            final var line = lines.get(i);
            final var localX = x + 32;
            final var localY = y + line.getHeight() * i;
            graphics.fill(localX + 3, localY + 1, localX + 8, localY + 6, 0xFF000000 + line.color.decimal());
            graphics.drawString(font, line.component, localX + 11, localY, 11184810, true);
        }
    }

    private static List<ChartElement> buildChart(ItemStack stack, EssenceProvider provider) {
        return provider.streamSorted()
                .map(entry -> new ChartElement(
                        entry.getKey().color(),
                        entry.getIntValue()))
                .toList();
    }

    private static List<LineInfo> buildLines(ItemStack stack, EssenceProvider provider) {
        return provider.streamSorted()
                .map(entry -> new LineInfo(
                        entry.getKey().color(),
                        Component.literal("%s x%s".formatted(
                                entry.getKey().displayName().getString(),
                                entry.getIntValue()))))
                .toList();
    }

    public record LineInfo(RGB color, Component component) {

        public int getHeight() {
            return 10;
        }

        public int getWidth(Font font) {
            return 11 + font.width(component);
        }
    }
}
