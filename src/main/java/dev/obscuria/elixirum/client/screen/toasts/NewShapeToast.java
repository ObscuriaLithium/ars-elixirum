package dev.obscuria.elixirum.client.screen.toasts;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.alchemy.styles.Shape;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public final class NewShapeToast extends NewStyleToast<Shape> {

    private static final Component TITLE_SINGLE = Component.translatable("toast.elixirum.new_shape.single");
    private static final Component TITLE_MULTIPLE = Component.translatable("toast.elixirum.new_shape.multiple");

    public static void addOrUpdate(ToastComponent component, Shape shape) {
        NewStyleToast.addOrUpdate(component, shape, NewShapeToast.class, () -> new NewShapeToast(shape));
    }

    private NewShapeToast(Shape shape) {
        addItem(shape);
    }

    @Override
    protected Component getTitle() {
        return isSingle() ? TITLE_SINGLE : TITLE_MULTIPLE;
    }

    @Override
    protected Component getDisplayName(Shape item) {
        return item.displayName();
    }

    @Override
    protected void renderIcon(GuiGraphics graphics, ToastComponent component, Shape shape) {
        var texture = ArsElixirum.identifier("textures/item/elixir/" + shape.texture + ".png");
        graphics.pose().translate(16.5, 16.5, 0);
        graphics.pose().scale(1.5f, 1.5f, 1.5f);
        graphics.pose().translate(-0.5, -1.5, 0);
        graphics.blit(texture, -8, -8, 0, 0, 16, 16, 16, 16);
    }
}