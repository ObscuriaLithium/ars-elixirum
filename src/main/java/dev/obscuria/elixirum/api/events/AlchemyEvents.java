package dev.obscuria.elixirum.api.events;

import dev.obscuria.elixirum.common.events._ElixirumEventRegistry;

public final class AlchemyEvents {

    public static final MasteryListener MASTERY = invokerOf(MasteryListener.class);

    public static <T> void register(Class<T> event, T listener) {
        _ElixirumEventRegistry.register(event, listener);
    }

    public static <T> T invokerOf(Class<T> event) {
        return _ElixirumEventRegistry.get(event).invoker();
    }
}
