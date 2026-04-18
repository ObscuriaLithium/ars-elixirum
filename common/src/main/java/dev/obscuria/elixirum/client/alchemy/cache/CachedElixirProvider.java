package dev.obscuria.elixirum.client.alchemy.cache;

@FunctionalInterface
public interface CachedElixirProvider {

    CachedElixirProvider EMPTY = CachedElixir::empty;

    CachedElixir elixir();
}
