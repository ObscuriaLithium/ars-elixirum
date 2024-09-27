package dev.obscuria.elixirum.client.screen.section.compendium;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.ElixirumPalette;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

final class SubStepItem extends HierarchicalWidget {
    private final ItemStack stack;
    private @Nullable MultiLineLabel label;

    public SubStepItem(Item item, Component content) {
        super(0, 0, 0, 0, content);
        this.setUpdateFlags(UPDATE_BY_WIDTH);
        this.stack = item.getDefaultInstance();
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (label == null) return;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 0.2f);
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_LIGHT, getX(), getY(), getWidth(), getHeight());
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_LIGHT, getX(), getY(), 18, getHeight());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        graphics.renderFakeItem(stack, getX() + 1, getY() + getHeight() / 2 - 8);
        this.label.renderLeftAligned(graphics, getX() + 23, getY() + 6, 10, ElixirumPalette.LIGHT);
    }

    @Override
    protected void reorganize() {
        this.label = MultiLineLabel.create(Minecraft.getInstance().font, getMessage(), getWidth() - 28);
        this.setHeight(10 + 10 * label.getLineCount() - 1);
    }
}
