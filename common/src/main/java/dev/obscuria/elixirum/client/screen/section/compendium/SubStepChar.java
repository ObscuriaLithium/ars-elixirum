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

final class SubStepChar extends HierarchicalWidget
{
    private final char index;
    private @Nullable MultiLineLabel label;

    public SubStepChar(char index, Component content)
    {
        super(0, 0, 0, 0, content);
        this.setUpdateFlags(UPDATE_BY_WIDTH);
        this.index = index;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        if (label == null) return;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 0.2f);
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_LIGHT, getX(), getY(), getWidth(), getHeight());
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_LIGHT, getX(), getY(), 15, getHeight());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        graphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(index)),
                getX() + 5, getY() + getHeight() / 2 - 4, ElixirumPalette.WHITE);
        this.label.renderLeftAligned(graphics, getX() + 20, getY() + 6, 10, ElixirumPalette.LIGHT);
    }

    @Override
    protected void reorganize()
    {
        this.label = MultiLineLabel.create(Minecraft.getInstance().font, getMessage(), getWidth() - 25);
        this.setHeight(10 + 10 * label.getLineCount() - 1);
    }
}
