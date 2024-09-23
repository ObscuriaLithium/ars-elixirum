package dev.obscuria.elixirum.client.screen.widget;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.ElixirumClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class ElixirOverview extends AbstractWidget {
    private static final ResourceLocation SHINE_TEXTURE = Elixirum.key("textures/gui/shine.png");
    private @Nullable ItemStack stack;

    public ElixirOverview(int x, int y) {
        super(x, y, 0, 0, Component.empty());
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.stack == null) return;
        final var minecraft = Minecraft.getInstance();
        final var timer = ElixirumClient.getSeconds();

        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        graphics.pose().translate(getX(), getY(), 0);
        graphics.pose().scale(1f, 1f, 1f);
        graphics.blit(SHINE_TEXTURE, -128, -256, 0, 0, 256, 256, 256, 256);
        graphics.pose().popPose();
        RenderSystem.disableBlend();

        graphics.pose().pushPose();
        graphics.pose().translate(getX(), getY() - 80, 150);
        graphics.pose().scale(8, 8, 8);
        graphics.pose().mulPose(Axis.YP.rotation(timer * 0.5f));
        graphics.pose().pushPose();
        graphics.pose().scale(16, -16, 16);
        graphics.pose().translate(-0.03125, 0, 0);
        final var model = minecraft.getItemRenderer().getModel(stack, null, null, 0);
        minecraft.getItemRenderer().render(
                stack, ItemDisplayContext.GUI, false, graphics.pose(),
                graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, model);
        Lighting.setupForFlatItems();
        graphics.flush();
        graphics.pose().popPose();
        graphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
