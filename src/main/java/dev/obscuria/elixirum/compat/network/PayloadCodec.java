//? if >=1.21 {
/*package dev.obscuria.elixirum.compat.network;

import com.mojang.serialization.Codec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.nbt.NbtOps;

import java.util.function.Supplier;

public record PayloadCodec<T>(Codec<T> codec, Supplier<RegistryAccess> registries) {
    public static <T> PayloadCodec<T> registryFriendly(Codec<T> codec, Supplier<RegistryAccess> registries) {
        return new PayloadCodec<>(codec, registries);
    }

    public void write(FriendlyByteBuf buffer, T value) {
        buffer.writeNbt(codec.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries.get()), value).getOrThrow());
    }

    public T read(FriendlyByteBuf buffer) {
        var tag = buffer.readNbt();
        if (tag == null) throw new IllegalArgumentException("Missing Ars Elixirum payload");
        return codec.parse(RegistryOps.create(NbtOps.INSTANCE, registries.get()), tag).getOrThrow();
    }
}
*///? }
