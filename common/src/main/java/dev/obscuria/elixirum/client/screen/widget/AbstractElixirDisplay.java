package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractElixirDisplay extends HierarchicalWidget {
    private final ItemStack stack;
    private float highlight;
    private float highlightO;

    public AbstractElixirDisplay(ItemStack stack) {
        super(0, 0, 15, 19, Component.empty());
        this.stack = stack;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void tick() {
        this.highlightO = this.highlight;
        if (this.isHovered) {
            this.highlight = 1F;
        } else if (this.highlight > 0) {
            this.highlight -= 0.1F;
        }
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (!transform.isWithinScissor()) {
            this.highlight = 0;
            return;
        }
        ElixirumScreen.debugRenderer(this, graphics, transform, mouseX, mouseY);
        if (transform.isMouseOver(mouseX, mouseY)) this.highlight = 1F;
        final var highlight = getHighlight();
        if (highlight > 0.001f) {
            graphics.pose().pushPose();
            graphics.pose().translate(getX() + (int) (getWidth() / 2.0), getY() + (int) (getHeight() / 2.0), 0);
            graphics.pose().scale(highlight, highlight, highlight);
            graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_PURPLE, getWidth() / -2, getHeight() / -2, getWidth(), getHeight());
            graphics.pose().popPose();
        }
        graphics.renderItem(stack, getX() - 1, getY() + 1);
        if (this.isSelected())
            graphics.blitSprite(ElixirumScreen.SPRITE_OUTLINE_WHITE, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    protected void reorganize() {

    }

    @Override
    protected boolean hasContents() {
        return true;
    }

    protected abstract boolean isSelected();

    private float getHighlight() {
        final var delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        return (float) Math.pow(Mth.lerp(delta, highlightO, highlight), 2);
    }
}
