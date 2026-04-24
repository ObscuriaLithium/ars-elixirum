package dev.obscuria.elixirum.client.screen.tooltip.components;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ChartElement;
import dev.obscuria.elixirum.client.screen.ChartRenderer;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CompositionChartComponent implements ClientTooltipComponent {

    private static final RGB COLOR_GRAY;
    private static final Component UNKNOWN_ENTRY;

    private final ItemStack stack;
    private final Aspect aspect;
    private final List<ChartElement> elements;
    private final List<LineInfo> lines;

    public CompositionChartComponent(ItemStack stack, AlchemyProperties properties) {
        this.stack = stack;
        this.aspect = properties.aspect();
        this.elements = properties.streamSorted().map(this::entryToElement).toList();
        this.lines = properties.streamSorted().map(this::entryToLine).toList();
    }

    @Override
    public int getHeight() {
        var totalLineHeight = 0;
        for (var line : lines) totalLineHeight += line.getHeight();
        return Math.max(32, totalLineHeight);
    }

    @Override
    public int getWidth(Font font) {
        var maxLineWidth = 0;
        for (var line : lines) maxLineWidth = Math.max(maxLineWidth, line.getWidth(font));
        return 32 + maxLineWidth;
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
            graphics.drawString(font, line.component, localX + 11, localY, 0xffffff, true);
        }
    }

    private ChartElement entryToElement(Object2IntMap.Entry<EssenceHolder> entry) {
        var isKnown = isKnown(entry.getKey());
        return new ChartElement(
                isKnown ? entry.getKey().color() : COLOR_GRAY, entry.getIntValue());
    }

    private LineInfo entryToLine(Object2IntMap.Entry<EssenceHolder> entry) {
        var isKnown = isKnown(entry.getKey());
        return new LineInfo(
                isKnown ? entry.getKey().color() : COLOR_GRAY,
                isKnown ? formatLine(entry) : UNKNOWN_ENTRY);
    }

    private Component formatLine(Object2IntMap.Entry<EssenceHolder> entry) {
        return Component.translatable("tooltip.composition_entry.format",
                entry.getKey().displayName().getString(),
                entry.getIntValue()
        ).withStyle(ChatFormatting.GRAY);
    }

    private boolean isKnown(EssenceHolder holder) {
        return stack.is(ElixirumItems.EXTRACT.asItem())
                || DiscoverySystem.isEssenceKnown(ClientAlchemy.INSTANCE.localProfile(), stack.getItem(), holder);
    }

    public record LineInfo(RGB color, Component component) {

        public int getHeight() {
            return 10;
        }

        public int getWidth(Font font) {
            return 11 + font.width(component);
        }
    }

    static {
        COLOR_GRAY = Colors.rgbOf(5592405);
        UNKNOWN_ENTRY = Component.translatable("tooltip.composition_entry.unknown").withStyle(ChatFormatting.DARK_GRAY);
    }
}
