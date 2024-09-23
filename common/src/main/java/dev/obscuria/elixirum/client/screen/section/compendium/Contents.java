package dev.obscuria.elixirum.client.screen.section.compendium;

import dev.obscuria.elixirum.client.screen.ElixirumPalette;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

final class Contents extends HierarchicalWidget {
    private final ContentsType type;
    private @Nullable MultiLineLabel label;
    private float highlight;
    private float highlightO;

    public Contents(ContentsType type) {
        super(0, 0, 0, 0, Component.empty());
        this.setUpdateFlags(UPDATE_BY_WIDTH);
        this.type = type;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (label == null) return;
        this.isHovered = transform.isMouseOver(mouseX, mouseY);
        final var highlight = getHighlight();
        if (highlight > 0.001f) {
            graphics.pose().pushPose();
            graphics.pose().translate(getX(), getY() + 1, 0);
            graphics.pose().scale(highlight, 1, 1);
            graphics.fill(0, 0, getWidth(), getHeight() - 2, ElixirumPalette.purple(0x2F));
            graphics.pose().popPose();
        }
        graphics.fill(getX(), getY() + 1, getX() + 2, getBottom() - 1,
                !RootCompendium.isSelected(type)
                        ? FastColor.ARGB32.lerp(highlight, ElixirumPalette.MEDIUM, ElixirumPalette.PURPLE)
                        : ElixirumPalette.WHITE);
        label.renderLeftAligned(graphics, getX() + 6, getY() + 4, 9,
                !RootCompendium.isSelected(type)
                        ? FastColor.ARGB32.lerp(highlight, ElixirumPalette.LIGHT, ElixirumPalette.PURPLE)
                        : ElixirumPalette.WHITE);
    }

    @Override
    public void tick() {
        this.highlightO = this.highlight;
        this.highlight = Math.clamp(isHovered ? highlight + 0.5f : highlight - 0.2f, 0f, 1f);
    }

    @Override
    protected void reorganize() {
        this.label = MultiLineLabel.create(Minecraft.getInstance().font, type.getDisplayName(), getWidth() - 6);
        this.setHeight(6 + 10 * label.getLineCount() - 1);
    }

    private float getHighlight() {
        final var delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        return (float) Math.pow(Mth.lerp(delta, highlightO, highlight), 2);
    }
}
