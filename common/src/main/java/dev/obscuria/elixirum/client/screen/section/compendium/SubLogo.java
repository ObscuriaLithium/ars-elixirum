package dev.obscuria.elixirum.client.screen.section.compendium;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

final class SubLogo extends HierarchicalWidget
{
    private static final ResourceLocation LOGO = Elixirum.key("textures/logo.png");

    public SubLogo()
    {
        super(0, 0, 0, 70, Component.empty());
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        graphics.pose().translate(getX() + getWidth() / 2f, getY() + 35f, 0);
        graphics.blit(LOGO, -112, -26, 0, 0, 225, 52, 225, 52);
        graphics.pose().popPose();
        RenderSystem.disableBlend();
    }

    @Override
    protected void reorganize()
    {

    }
}
