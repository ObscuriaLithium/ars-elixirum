package dev.obscuria.elixirum.common.world;

import com.mojang.serialization.Codec;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicCodecs {

    public static final Codec<AtomicInteger> INT;

    static {
        INT = Codec.INT.xmap(AtomicInteger::new, AtomicInteger::get);
    }
}
