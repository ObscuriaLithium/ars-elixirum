package dev.obscuria.elixirum.common.events;

import java.util.HashMap;
import java.util.Map;

public final class _ElixirumEventRegistry {

    private static final Map<Class<?>, Object> LISTENERS = new HashMap<>();

    public static <T> void register(Class<T> type, T listener) {
        get(type).addListener(listener);
    }

    @SuppressWarnings("unchecked")
    public static <T> _EventInvoker<T> get(Class<T> type) {
        return (_EventInvoker<T>) LISTENERS.computeIfAbsent(type, _EventInvoker::new);
    }
}
