package dev.obscuria.elixirum.server.alchemy.generation.operations;

public record ProposeOperation<T>(T value) implements Operation<T> {}
