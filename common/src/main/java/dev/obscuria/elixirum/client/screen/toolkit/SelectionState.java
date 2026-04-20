package dev.obscuria.elixirum.client.screen.toolkit;

import dev.obscuria.fragmentum.util.signal.Signal1;

import java.util.Objects;
import java.util.function.Consumer;

public final class SelectionState<T> {

    private final Signal1<T> changed = new Signal1<>();
    private T value;

    public SelectionState(T initial) {
        this.value = initial;
    }

    public boolean is(T value) {
        return Objects.equals(this.value, value);
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        if (Objects.equals(value, newValue)) return;
        value = newValue;
        changed.emit(newValue);
    }

    public void listen(Object source, Consumer<T> listener) {
        changed.connect(source, false, null, listener::accept);
    }
}
