package dev.obscuria.elixirum.common;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.fragmentum.FragmentumProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;

public class CodecHelper {

    public static <T> void save(CompoundTag tag, String key, Codec<T> codec, T value) {
        final var ops = RegistryOps.create(NbtOps.INSTANCE, FragmentumProxy.registryAccess());
        codec.encodeStart(ops, value)
                .resultOrPartial(error -> warn(error, "encode", key, value))
                .ifPresent(it -> tag.put(key, it));
    }

    public static <T> T load(CompoundTag tag, String key, Codec<T> codec, T fallback) {
        if (!tag.contains(key)) return fallback;
        final var ops = RegistryOps.create(NbtOps.INSTANCE, FragmentumProxy.registryAccess());
        return codec.decode(ops, tag.get(key))
                .resultOrPartial(error -> warn(error, "decode", key, fallback))
                .map(Pair::getFirst).orElse(fallback);
    }

    private static void warn(String error, String operation, String key, Object value) {
        final var type = value.getClass().getSimpleName();
        ArsElixirum.LOGGER.warn("Failed to {} `{}` of type {}: {}", operation, key, type, error);
    }
}
