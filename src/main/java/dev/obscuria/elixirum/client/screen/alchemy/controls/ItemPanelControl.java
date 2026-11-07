package dev.obscuria.elixirum.client.screen.alchemy.controls;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemStack;

public class ItemPanelControl extends Control {

    private final ItemStack stack;

    public ItemPanelControl(ItemStack stack) {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
        this.stack = stack;
        setSizeHints(SIZE_HINT_WIDTH);
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        context.pushModulate(Palette.DARKEST);
        GuiToolkit.draw(graphics, Textures.SOLID_WHITE, getX(), getY(), 18, getHeight());
        context.popModulate();
        graphics.pose().pushPose();
        graphics.pose().translate(getX() + 9, getY() + getHeight() * 0.5f, 0);
        graphics.renderItem(stack, -8, -8);
        graphics.pose().popPose();
        this.renderChildren(graphics, context, mouseX, mouseY);
    }

    @Override
    protected void measure() {
        var maxHeight = 0;
        for (var child : getChildren()) {
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
        }
        setRequiredHeight(maxHeight + 8);
    }

    @Override
    protected void layout() {
        for (var child : getChildren()) {
            placeChild(child, getX() + 22, getY() + 4, getWidth() - 22, child.getMeasuredHeight());
        }
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }
}
