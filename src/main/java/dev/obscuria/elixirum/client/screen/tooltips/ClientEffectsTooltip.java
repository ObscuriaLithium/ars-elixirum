package dev.obscuria.elixirum.client.screen.tooltips;

import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.basics.Aspect;
import dev.obscuria.elixirum.common.world.tooltip.EffectsTooltip;
import dev.obscuria.elixirum.client.screen.ChartElement;
import dev.obscuria.elixirum.client.screen.ChartRenderer;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ClientEffectsTooltip(
        Aspect aspect,
        List<ChartElement> elements,
        List<ClientCompositionTooltip.LineInfo> lines
) implements ClientTooltipComponent {

    private static final RGB SIDE_EFFECT_COLOR = Colors.rgbOf(11184810);

    public static ClientTooltipComponent of(EffectsTooltip tooltip) {
        return new ClientCompositionTooltip(
                tooltip.effects().aspect(),
                buildChart(tooltip.stack(), tooltip.effects()),
                buildLines(tooltip.stack(), tooltip.effects()));
    }

    @Override
    public int getHeight() {
        return Math.max(32, lines.stream().mapToInt(ClientCompositionTooltip.LineInfo::getHeight).sum()) + 3;
    }

    @Override
    public int getWidth(Font font) {
        return 32 + lines.stream().mapToInt(it -> it.getWidth(font)).max().orElse(0);
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        ChartRenderer.renderChart(graphics, x, y, 32, elements);
        graphics.blit(aspect.texture, x + 7, y + 7, 0, 0, 18, 18, 18, 18);
        for (var i = 0; i < lines.size(); i++) {
            final var line = lines.get(i);
            final var localX = x + 32;
            final var localY = y + line.getHeight() * i;
            graphics.fill(localX + 3, localY + 1, localX + 8, localY + 6, 0xFF000000 + line.color().decimal());
            graphics.drawString(font, line.component(), localX + 11, localY, 11184810, true);
        }
    }

    private static List<ChartElement> buildChart(ItemStack stack, ElixirContents effects) {
        var result = new ArrayList<ChartElement>();
        var sideEffectWeight = 0.0;

        for (var provider : effects.effects()) {
            if (provider.isVoided()) {
                sideEffectWeight += provider.quality();
            } else {
                result.add(new ChartElement(provider.color(), (int) provider.quality()));
            }
        }

        if (sideEffectWeight > 0) {
            result.add(new ChartElement(SIDE_EFFECT_COLOR, (float) sideEffectWeight));
        }

        return result;
    }

    private static List<ClientCompositionTooltip.LineInfo> buildLines(ItemStack stack, ElixirContents effects) {
        var result = new ArrayList<ClientCompositionTooltip.LineInfo>();
        var sideEffectCount = 0;
        var sideEffectWeight = 0.0;

        for (var provider : effects.effects()) {
            if (provider.isVoided()) {
                sideEffectCount += 1;
                sideEffectWeight += provider.quality();
            } else {
                result.add(new ClientCompositionTooltip.LineInfo(
                        provider.color(), Component
                        .translatable("potion.withDuration",
                                provider.displayNameWithPotency(),
                                provider.statusOrDuration())
                        .withStyle(provider.mobEffect().getCategory().getTooltipFormatting())));
            }
        }

        if (sideEffectCount > 0) {
            result.add(new ClientCompositionTooltip.LineInfo(
                    SIDE_EFFECT_COLOR,
                    Component.literal(sideEffectCount + " Side Effects")
                            .withStyle(ChatFormatting.GRAY)));
        }

        return result;
    }
}
