package dev.obscuria.elixirum.server.alchemy.generation;

import dev.obscuria.elixirum.server.alchemy.generation.operations.Operation;

import java.util.Optional;

@FunctionalInterface
public interface GenerationStep<K, V> {

    Optional<Operation<V>> apply(K key);
}
