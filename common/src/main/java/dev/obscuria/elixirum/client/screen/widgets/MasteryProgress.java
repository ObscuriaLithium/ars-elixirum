package dev.obscuria.elixirum.client.screen.widgets;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.world.tooltip.TooltipOptions;
import dev.obscuria.fragmentum.world.tooltip.Tooltips;
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

import java.util.ArrayList;
import java.util.List;

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
        int level = ClientAlchemy.INSTANCE.localProfile().mastery().masteryLevel().get();
        int total = ClientAlchemy.INSTANCE.localProfile().mastery().xpForNextLevel();
        int current = ClientAlchemy.INSTANCE.localProfile().mastery().masteryXp().get();
        double ratio = (double) current / total;

        GuiGraphicsUtil.setShaderColor(Colors.argbOf(0x40384A));
        GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.PROGRESS, getX(), getY() + 10, width, 4);
        GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.ACCENT);
        GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.PROGRESS, getX(), getY() + 10, (int) (width * ratio), 4);
        GuiGraphicsUtil.setShaderColor(Colors.argbOf(0xffffffff));

        int iconIndex = Math.min(Math.max(level / 25, 0), 3);
        graphics.blit(MASTERY, getX() - 12, getY() + 1, 24f * iconIndex, 0f, 24, 24, 96, 24);

        graphics.pose().pushPose();
        graphics.pose().translate(this.getX(), this.getY() + 9.0, 0.0);
        graphics.pose().scale(0.66f, 0.66f, 0.66f);
        graphics.drawCenteredString(font, Component.literal(String.valueOf(level)).withStyle(Style.EMPTY.withBold(true)), 0, 0, 0xFFFFFF);
        graphics.pose().popPose();

        graphics.drawCenteredString(font, "%s/%s".formatted(current, total), getX() + 50, getY() + 8, 0xFFFFFF);

        if (this.isHovered()) {
            AbstractPage.tooltip = this.tooltip;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    private List<Component> getCustomTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("gui.elixirum.progress.title"));

        Component description = Component.translatable(
                "gui.elixirum.progress",
                0, //ClientAlchemy.localProfile().knowledge.totalKnownEssences,
                0, //ClientAlchemy.ingredients().totalEssences,
                0);//ClientAlchemy.ingredients().totalIngredients);

        tooltip.addAll(Tooltips.process(description, TooltipOptions.DESCRIPTION));
        return tooltip;
    }

    private static final ResourceLocation MASTERY = ArsElixirum.identifier("textures/gui/mastery.png");
    private static final ResourceLocation PROGRESS_BAR = ArsElixirum.identifier("textures/gui/progress_bar.png");
    private static final ResourceLocation PROGRESS_BACK = ArsElixirum.identifier("textures/gui/progress_back.png");
}
