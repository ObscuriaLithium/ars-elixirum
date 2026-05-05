package dev.obscuria.elixirum.client.screen.toasts;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class NewStyleToast<T> implements Toast {

    private final List<T> items = Lists.newArrayList();
    private long lastChanged;
    private boolean changed;

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent component, long time) {
        if (this.changed) {
            this.lastChanged = time;
            this.changed = false;
        }
        if (items.isEmpty()) return Visibility.HIDE;

        var idx = (int) (time / Math.max(1.0, 5000.0 * component.getNotificationDisplayTimeMultiplier() / items.size()) % items.size());
        var item = items.get(idx);

        graphics.blit(Toast.TEXTURE, 0, 0, 0, 0, this.width(), this.height());
        graphics.pose().pushPose();
        renderIcon(graphics, component, item);
        graphics.pose().popPose();
        graphics.drawString(component.getMinecraft().font, getTitle(), 30, 7, 16755200, false);
        graphics.drawString(component.getMinecraft().font, getDisplayName(item), 30, 18, 0xffffff, false);

        var isExpired = (time - lastChanged) >= 5000.0 * component.getNotificationDisplayTimeMultiplier();
        return isExpired ? Visibility.HIDE : Visibility.SHOW;
    }

    protected abstract Component getTitle();

    protected abstract Component getDisplayName(T item);

    protected abstract void renderIcon(GuiGraphics graphics, ToastComponent component, T item);

    protected void addItem(T item) {
        this.items.add(item);
        this.changed = true;
    }

    protected boolean isSingle() {
        return items.size() == 1;
    }

    protected static <T, S extends NewStyleToast<T>> void addOrUpdate(
            ToastComponent component, T item, Class<S> toastClass, Supplier<S> factory
    ) {
        @Nullable S toast = component.getToast(toastClass, NO_TOKEN);
        if (toast == null) component.addToast(factory.get());
        else toast.addItem(item);
    }
}