package dev.obscuria.elixirum;

import org.jetbrains.annotations.ApiStatus;

public final class NeoElixirumClient {

    @ApiStatus.Internal
    public static void init() {
        ElixirumClient.init();
    }
}
