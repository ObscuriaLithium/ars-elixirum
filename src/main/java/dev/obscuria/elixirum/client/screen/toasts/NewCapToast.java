package dev.obscuria.elixirum.client.screen.toasts;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.alchemy.styles.Cap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public final class NewCapToast extends NewStyleToast<Cap> {

    private static final Component TITLE_SINGLE = Component.translatable("toast.elixirum.new_cap.single");
    private static final Component TITLE_MULTIPLE = Component.translatable("toast.elixirum.new_cap.multiple");

    public static void addOrUpdate(ToastComponent component, Cap cap) {
        NewStyleToast.addOrUpdate(component, cap, NewCapToast.class, () -> new NewCapToast(cap));
    }

    private NewCapToast(Cap cap) {
        addItem(cap);
    }

    @Override
    protected Component getTitle() {
        return isSingle() ? TITLE_SINGLE : TITLE_MULTIPLE;
    }

    @Override
    protected Component getDisplayName(Cap item) {
        return item.displayName();
    }

    @Override
    protected void renderIcon(GuiGraphics graphics, ToastComponent component, Cap cap) {
        var texture = ArsElixirum.identifier("textures/item/elixir/" + cap.texture + ".png");
        graphics.pose().translate(16, 16, 0);
        graphics.pose().scale(2, 2, 2);
        graphics.pose().translate(-0.5, 5, 0);
        graphics.blit(texture, -8, -8, 0, 0, 16, 16, 16, 16);
    }
}
