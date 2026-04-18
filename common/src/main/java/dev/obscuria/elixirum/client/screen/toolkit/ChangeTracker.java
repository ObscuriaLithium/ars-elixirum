package dev.obscuria.elixirum.client.screen.toolkit;

import java.util.Objects;

public class ChangeTracker<T> {

    private T last;
    private T current;

    public ChangeTracker(T initial) {
        this.last = initial;
        this.current = initial;
    }

    public T value() {
        return current;
    }

    public void update(T value) {
        this.current = value;
    }

    public boolean consumeUpdates() {
        if (Objects.equals(current, last)) return false;
        this.last = current;
        return true;
    }
}
