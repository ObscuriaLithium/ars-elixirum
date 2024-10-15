package dev.obscuria.elixirum;

import dev.obscuria.core.api.ObscureAPI;
import dev.obscuria.core.api.v1.common.config.IConfigValue;

public final class ElixirumConfig
{
    public static final IConfigValue<Boolean> testBoolean;
    public static final IConfigValue<Integer> testInteger;

    static
    {
        final var builder = ObscureAPI.PLATFORM.createConfig();

        builder.push("TestSection");
        testBoolean = builder.define("testBoolean", true);
        testInteger = builder.defineInRange("testInteger", 5, 0, 10);
        builder.pop();

        builder.build();
    }
}
