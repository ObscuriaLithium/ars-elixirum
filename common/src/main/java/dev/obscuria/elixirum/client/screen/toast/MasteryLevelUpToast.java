package dev.obscuria.elixirum.client.screen.toast;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MasteryLevelUpToast implements Toast {

    private static final ResourceLocation MASTERY_TEXTURE = ArsElixirum.identifier("textures/gui/mastery.png");
    private static final Component TITLE = Component.translatable("toast.elixirum.level_up.title");
    private static final Component DESCRIPTION = Component.translatable("toast.elixirum.level_up.description");

    private final int masteryLevel;
    private boolean soundPlayed;

    public MasteryLevelUpToast(int masteryLevel) {
        this.masteryLevel = masteryLevel;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent component, long time) {
        this.maybePlaySound(component);
        graphics.blit(Toast.TEXTURE, 0, 0, 0, 0, this.width(), this.height());
        renderMasteryIcon(graphics, 16, 16);
        graphics.drawString(component.getMinecraft().font, TITLE, 30, 7, 16746751, false);
        graphics.drawString(component.getMinecraft().font, DESCRIPTION, 30, 18, 0xffffff, false);

        var isExpired = time >= 5000.0 * component.getNotificationDisplayTimeMultiplier();
        return isExpired ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    private void renderMasteryIcon(GuiGraphics graphics, int x, int y) {
        var font = Minecraft.getInstance().font;
        var iconIndex = Mth.clamp(masteryLevel / 25, 0, 3);
        var counter = Component.literal(String.valueOf(masteryLevel)).withStyle(Style.EMPTY.withBold(true));
        graphics.blit(MASTERY_TEXTURE, x - 12, y - 12, 24 * iconIndex, 0F, 24, 24, 96, 24);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y - 4, 0f);
        graphics.pose().scale(0.66f, 0.66f, 0.66f);
        graphics.drawCenteredString(font, counter, 0, 0, -0x1);
        graphics.pose().popPose();
    }

    private void maybePlaySound(ToastComponent component) {
        if (soundPlayed) return;
        this.soundPlayed = true;
        var sound = SimpleSoundInstance.forUI(ElixirumSounds.UI_MASTERY_LEVEL_UP, 1F);
        component.getMinecraft().getSoundManager().play(sound);
    }
}
