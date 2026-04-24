package dev.obscuria.elixirum.common.events;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public final class _EventInvoker<T> {

    private final List<T> listeners = new ArrayList<>();
    private final T proxy;

    @SuppressWarnings("all")
    public _EventInvoker(Class<T> type) {
        this.proxy = (T) Proxy.newProxyInstance(
                type.getClassLoader(), new Class[]{type},
                (self, method, args) -> {
                    for (var listener : listeners)
                        method.invoke(listener, args);
                    return null;
                }
        );
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public T invoker() {
        return proxy;
    }
}