package dev.obscuria.elixirum.client.screen.section.compendium;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.ElixirumPalette;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

final class SubNote extends HierarchicalWidget {
    private @Nullable MultiLineLabel label;

    public SubNote(Component content) {
        super(0, 0, 0, 0, content);
        this.setUpdateFlags(UPDATE_BY_WIDTH);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (label == null) return;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 0.2f);
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_PURPLE, getX(), getY(), getWidth(), getHeight());
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_PURPLE, getX(), getY(), getWidth(), 15);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        graphics.drawString(Minecraft.getInstance().font, Component.literal("ⓘ Note"), getX() + 5, getY() + 4, ElixirumPalette.WHITE);
        this.label.renderLeftAligned(graphics, getX() + 5, getY() + 21, 10, ElixirumPalette.PURPLE);
    }

    @Override
    protected void reorganize() {
        this.label = MultiLineLabel.create(Minecraft.getInstance().font, getMessage(), getWidth() - 10);
        this.setHeight(25 + 10 * label.getLineCount() - 1);
    }
}
