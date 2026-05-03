package dev.obscuria.elixirum.client.screen.toasts;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.alchemy.styles.Cap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public record NewCapToast(Cap cap) implements Toast {

    private static final Component TITLE = Component.literal("New Cap Unlocked!");

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent component, long time) {
        var texture = ArsElixirum.identifier("textures/item/elixir/" + cap.texture + ".png");
        graphics.blit(Toast.TEXTURE, 0, 0, 0, 0, this.width(), this.height());
        graphics.pose().pushPose();
        graphics.pose().translate(16, 16, 0);
        graphics.pose().scale(2, 2, 2);
        graphics.pose().translate(-0.5, 5, 0);
        graphics.blit(texture, -8, -8, 0, 0, 16, 16, 16, 16);
        graphics.pose().popPose();
        graphics.drawString(component.getMinecraft().font, TITLE, 30, 7, 16755200, false);
        graphics.drawString(component.getMinecraft().font, cap.displayName(), 30, 18, 0xffffff, false);

        var isExpired = time >= 5000.0 * component.getNotificationDisplayTimeMultiplier();
        return isExpired ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}
