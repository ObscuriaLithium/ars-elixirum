package dev.obscuria.elixirum.client.screen.toolkit.tools;

import dev.obscuria.fragmentum.util.signal.Signal1;

import java.util.Objects;
import java.util.function.Consumer;

public final class Selection<T> {

    private final Signal1<T> changed = new Signal1<>();
    private T value;

    public Selection(T initial) {
        this.value = initial;
    }

    public void addListener(Consumer<T> listener) {
        changed.connect(this, false, null, listener::accept);
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        if (Objects.equals(value, newValue)) return;
        value = newValue;
        changed.emit(newValue);
    }

    public boolean is(T value) {
        return Objects.equals(this.value, value);
    }
}
