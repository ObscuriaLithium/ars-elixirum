package dev.obscuria.elixirum.client.screen.alchemy;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.helpers.MasteryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class MasteryProgress extends AbstractWidget {

    private final Font font;
    private final Tooltip tooltip;

    public MasteryProgress(int x, int y) {
        super(x - 50, y + 10, 100, 24, CommonComponents.EMPTY);

        this.font = Minecraft.getInstance().font;
        this.tooltip = Tooltip.create(
                Component.empty()
                        .append(Component.translatable("ui.elixirum.mastery.title").withStyle(ChatFormatting.WHITE))
                        .append(CommonComponents.NEW_LINE)
                        .append(Component.translatable("ui.elixirum.mastery.description").withStyle(ChatFormatting.GRAY)));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int level = ClientAlchemy.localProfile().mastery().getLevel();
        int current = ClientAlchemy.localProfile().mastery().getXp();
        int total = MasteryHelper.calculateXpForLevel(level + 1);
        double ratio = (double) current / total;

        GuiToolkit.setShaderColor(Palette.DARK);
        GuiToolkit.draw(graphics, Textures.PROGRESS, getX(), getY() + 10, width, 4);
        GuiToolkit.setShaderColor(Palette.ACCENT);
        GuiToolkit.draw(graphics, Textures.PROGRESS, getX(), getY() + 10, (int) (width * ratio), 4);
        GuiToolkit.resetShaderColor();

        int iconIndex = Math.min(Math.max(level / 25, 0), 3);
        graphics.blit(MASTERY, getX() - 12, getY() + 1, 24f * iconIndex, 0f, 24, 24, 96, 24);

        graphics.pose().pushPose();
        graphics.pose().translate(this.getX(), this.getY() + 9.0, 0.0);
        graphics.pose().scale(0.66f, 0.66f, 0.66f);
        graphics.drawCenteredString(font, Component.literal(String.valueOf(level)).withStyle(Style.EMPTY.withBold(true)), 0, 0, 0xFFFFFF);
        graphics.pose().popPose();

        graphics.drawCenteredString(font, "%s/%s".formatted(current, total), getX() + 50, getY() + 8, 0xFFFFFF);

        if (this.isHovered()) {
            AlchemyScreen.tooltip = this.tooltip;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    private static final ResourceLocation MASTERY = ArsElixirum.identifier("textures/gui/mastery.png");
    private static final ResourceLocation PROGRESS_BAR = ArsElixirum.identifier("textures/gui/progress_bar.png");
    private static final ResourceLocation PROGRESS_BACK = ArsElixirum.identifier("textures/gui/progress_back.png");
}
