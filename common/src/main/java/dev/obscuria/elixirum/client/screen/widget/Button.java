package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class Button extends HierarchicalWidget
{
    protected static final ResourceLocation GRAY_SPRITE = Elixirum.key("button/gray");
    protected static final ResourceLocation PURPLE_SPRITE = Elixirum.key("button/purple");
    protected static final ResourceLocation GREEN_SPRITE = Elixirum.key("button/green");

    public Button(Component name)
    {
        super(0, 0, 0, 14, name);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        if (!transform.isWithinScissor()) return;
        this.isHovered = transform.isMouseOver(mouseX, mouseY);
        final var font = Minecraft.getInstance().font;
        this.renderButton(graphics, transform, mouseX, mouseY);
        graphics.drawCenteredString(font, getButtonName(), getX() + getWidth() / 2, getY() + 3, 0xFFFFFFFF);
    }

    protected void renderButton(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        graphics.blitSprite(isHovered ? PURPLE_SPRITE : GRAY_SPRITE, getX(), getY(), getWidth(), getHeight());
    }

    protected Component getButtonName()
    {
        return this.getMessage();
    }

    @Override
    protected void reorganize()
    {

    }
}
