package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.core.api.v1.common.text.TextWrapper;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class ProgressDisplay extends AbstractWidget
{
    private static final ResourceLocation PROGRESS_BACK = Elixirum.key("textures/gui/progress_back.png");
    private static final ResourceLocation PROGRESS = Elixirum.key("textures/gui/progress.png");

    public ProgressDisplay(int x, int y)
    {
        super(x - 50, y + 10, 100, 24, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        final var total = ClientAlchemy.getIngredients().getTotalEssences();
        final var discovered = ClientAlchemy.getProfile().getTotalDiscoveredEssences();
        final var ratio = discovered / 1.0 / total;
        graphics.blit(PROGRESS_BACK, getX(), getY() + 10, 0, 0, 100, 4, 100, 4);
        graphics.blit(PROGRESS, getX(), getY() + 10, 0, 0, (int) (100 * ratio), 4, 100, 4);
        graphics.drawCenteredString(
                Minecraft.getInstance().font,
                Component.literal("%s/%s".formatted(discovered, total)),
                getX() + getWidth() / 2, getY() + 8, 0xFFFFFFFF);
        if (isHovered)
            ElixirumScreen.tooltipProvider = this::getCustomTooltip;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {

    }

    private @Unmodifiable List<Component> getCustomTooltip()
    {
        final var tooltip = Lists.<Component>newArrayList();
        tooltip.add(Component.literal("Discovered Essences"));
        final var description = Component.translatable("elixirum.discovered_essences",
                ClientAlchemy.getProfile().getTotalDiscoveredEssences(),
                ClientAlchemy.getIngredients().getTotalEssences(),
                ClientAlchemy.getIngredients().getTotalIngredients());
        tooltip.addAll(TextWrapper.create(description)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                .withMaxLength(30)
                .build());
        return tooltip;
    }
}
