package dev.obscuria.elixirum.client.screen.tool;

import dev.obscuria.elixirum.client.screen.HierarchicalWidget;

@FunctionalInterface
public interface ClickAction<T extends HierarchicalWidget> {

    static <T extends HierarchicalWidget> Left<T> left(ClickAction<T> action) {
        return action::invoke;
    }

    static <T extends HierarchicalWidget> Right<T> right(ClickAction<T> action) {
        return action::invoke;
    }

    boolean invoke(T widget);

    @SuppressWarnings("unchecked")
    default boolean invokeCast(Object object) {
        return this.invoke((T) object);
    }

    default boolean canConsume(int button) {
        return true;
    }

    @FunctionalInterface
    interface Left<T extends HierarchicalWidget> extends ClickAction<T> {

        @Override
        default boolean canConsume(int button) {
            return button == 0;
        }
    }

    @FunctionalInterface
    interface Right<T extends HierarchicalWidget> extends ClickAction<T> {

        @Override
        default boolean canConsume(int button) {
            return button == 1;
        }
    }
}
