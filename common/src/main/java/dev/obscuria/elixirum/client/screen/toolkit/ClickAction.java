package dev.obscuria.elixirum.client.screen.toolkit;

import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;

import java.util.function.Consumer;

@FunctionalInterface
public interface ClickAction<T extends HierarchicalControl> {

    static <T extends HierarchicalControl> LeftClick<T> leftClick(Consumer<T> action) {
        return it -> {
            action.accept(it);
            return true;
        };
    }

    static <T extends HierarchicalControl> RightClick<T> rightClick(Consumer<T> action) {
        return it -> {
            action.accept(it);
            return true;
        };
    }

    static <T extends HierarchicalControl> LeftClick<T> flatLeftClick(ClickAction<T> action) {
        return action::invoke;
    }

    static <T extends HierarchicalControl> RightClick<T> flatRightClick(ClickAction<T> action) {
        return action::invoke;
    }

    boolean invoke(T node);

    @SuppressWarnings("unchecked")
    default boolean invokeCast(HierarchicalControl node) {
        try {
            return invoke((T) node);
        } catch (ClassCastException ignored) {
            return false;
        }
    }

    default boolean canConsume(int button) {
        return true;
    }

    default boolean mouseClicked(HierarchicalControl node, GlobalTransform transform, double mouseX, double mouseY, int button) {
        if (!transform.isMouseOver(mouseX, mouseY)) return false;
        return invokeCast(node);
    }

    interface LeftClick<T extends HierarchicalControl> extends ClickAction<T> {

        @Override
        default boolean canConsume(int button) {
            return button == 0;
        }
    }

    interface RightClick<T extends HierarchicalControl> extends ClickAction<T> {

        @Override
        default boolean canConsume(int button) {
            return button == 1;
        }
    }
}
