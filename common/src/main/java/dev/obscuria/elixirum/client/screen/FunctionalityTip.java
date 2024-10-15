package dev.obscuria.elixirum.client.screen;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ElixirumKeyMappings;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class FunctionalityTip extends AbstractWidget
{
    private static final ResourceLocation TEXTURE = Elixirum.key("textures/gui/functionality_tip.png");

    public FunctionalityTip(int x, int y)
    {
        super(x, y, 12, 12, Component.empty());
        final var key = ElixirumKeyMappings.MENU.getTranslatedKeyMessage();
        this.setTooltip(Tooltip.create(
                Component.literal(Elixirum.DISPLAY_NAME).withStyle(ChatFormatting.GOLD)
                        .append(Component.literal("\n"))
                        .append(Component.translatable("elixirum.functionality_tip", key).withStyle(ChatFormatting.GRAY))));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.blit(TEXTURE, getX(), getY(), 0, isHovered ? 12 : 0, 12, 12, 12, 24);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY)
    {
        Minecraft.getInstance().setScreen(new ElixirumScreen());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {

    }
}
