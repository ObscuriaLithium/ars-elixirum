package dev.obscuria.elixirum.client.screen.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public record NewChromaToast(Chroma chroma) implements Toast {

    private static final Component TITLE = Component.literal("New Chroma Unlocked!");

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent component, long time) {
        graphics.blit(Toast.TEXTURE, 0, 0, 0, 0, this.width(), this.height());
        graphics.pose().pushPose();
        graphics.pose().translate(16.5, 16.5, 0);
        graphics.pose().scale(1.5f, 1.5f, 1.5f);
        var rgb = chroma.computeColor(ElixirContents.water());
        float r = rgb.red();
        float g = rgb.green();
        float b = rgb.blue();
        RenderSystem.setShaderColor(r, g, b, 1f);
        GuiToolkit.draw(graphics, Textures.CHROMA, -6, -6, 12, 12);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        graphics.pose().popPose();
        graphics.drawString(component.getMinecraft().font, TITLE, 30, 7, 16755200, false);
        graphics.drawString(component.getMinecraft().font, chroma.displayName(), 30, 18, 0xffffff, false);

        var isExpired = time >= 5000.0 * component.getNotificationDisplayTimeMultiplier();
        return isExpired ? Visibility.HIDE : Visibility.SHOW;
    }
}
