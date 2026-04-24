package dev.obscuria.elixirum.api.events;

import dev.obscuria.elixirum.common.events._ElixirumEventRegistry;

public interface AlchemyEvents {

    MasteryListener MASTERY = invokerOf(MasteryListener.class);

    static <T> void register(Class<T> event, T listener) {
        _ElixirumEventRegistry.register(event, listener);
    }

    static <T> T invokerOf(Class<T> event) {
        return _ElixirumEventRegistry.get(event).invoker();
    }
}
