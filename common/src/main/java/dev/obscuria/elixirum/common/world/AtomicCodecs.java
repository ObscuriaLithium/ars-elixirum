package dev.obscuria.elixirum.common.world;

import com.mojang.serialization.Codec;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicCodecs {

    public static final Codec<AtomicBoolean> BOOL;
    public static final Codec<AtomicInteger> INT;

    static {
        BOOL = Codec.BOOL.xmap(AtomicBoolean::new, AtomicBoolean::get);
        INT = Codec.INT.xmap(AtomicInteger::new, AtomicInteger::get);
    }
}
