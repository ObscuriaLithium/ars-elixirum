package dev.obscuria.elixirum.client.screen.tool;

public record Accessor<T>(Getter<T> getter, Setter<T> setter) {

    public static <T> Accessor<T> create(Getter<T> getter, Setter<T> setter) {
        return new Accessor<>(getter, setter);
    }

    public T get() {
        return this.getter.get();
    }

    public void set(T value) {
        this.setter.set(value);
    }

    @FunctionalInterface
    public interface Getter<T> {
        T get();
    }

    public interface Setter<T> {
        void set(T t);
    }
}
