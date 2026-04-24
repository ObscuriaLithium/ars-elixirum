package dev.obscuria.elixirum.client.screen.tooltip;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ChartElement;
import dev.obscuria.elixirum.client.screen.ChartRenderer;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceProvider;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.tooltip.CompositionTooltip;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.ChatFormatting;
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

    private static final RGB COLOR_GRAY = Colors.rgbOf(5592405);

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
            graphics.drawString(font, line.component, localX + 11, localY, 0xffffff, true);
        }
    }

    private static List<ChartElement> buildChart(ItemStack stack, EssenceProvider provider) {
        return provider.streamSorted()
                .map(entry -> new ChartElement(
                        isKnown(stack, entry.getKey())
                                ? entry.getKey().color()
                                : COLOR_GRAY,
                        entry.getIntValue())).toList();
    }

    private static List<LineInfo> buildLines(ItemStack stack, EssenceProvider provider) {
        return provider.streamSorted()
                .map(entry -> new LineInfo(
                        isKnown(stack, entry.getKey())
                                ? entry.getKey().color()
                                : COLOR_GRAY,
                        isKnown(stack, entry.getKey())
                                ? Component.literal("%s x%s".formatted(entry.getKey().displayName().getString(), entry.getIntValue())).withStyle(ChatFormatting.GRAY)
                                : Component.literal("???").withStyle(ChatFormatting.DARK_GRAY)))
                .toList();
    }

    private static boolean isKnown(ItemStack stack, EssenceHolder holder) {
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
}
