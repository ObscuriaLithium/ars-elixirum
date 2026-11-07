package dev.obscuria.elixirum.client.screen.alchemy.pages.discoveries;

import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

record DiscoveryTarget(@Nullable Essence essence) {

    public static final DiscoveryTarget EMPTY = new DiscoveryTarget(null);

    public Essence get() {
        return Objects.requireNonNull(essence);
    }

    public boolean isEmpty() {
        return this.essence == null;
    }
}