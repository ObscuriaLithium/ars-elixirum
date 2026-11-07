//? if >=1.21 {
/*package dev.obscuria.elixirum.compat.archivist.nbt;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class NBTComponent<T> {

    private final String[] path;
    private final Codec<T> codec;

    private NBTComponent(String path, Codec<T> codec) {
        this.path = Objects.requireNonNull(path, "Path must not be null").split("\\.", -1);
        if (Arrays.stream(this.path).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Path must contain nonempty dotted segments");
        }
        this.codec = Objects.requireNonNull(codec, "Codec must not be null");
    }

    public static <T> Builder<T> createBuilder() {
        return new Builder<>();
    }

    public Optional<T> read(CompoundTag root) {
        Tag current = root;
        for (var segment : path) {
            if (!(current instanceof CompoundTag compound)) return Optional.empty();
            current = compound.get(segment);
            if (current == null) return Optional.empty();
        }
        return codec.parse(NbtOps.INSTANCE, current).result();
    }

    public T readOrDefault(CompoundTag root, T defaultValue) {
        return read(root).orElse(defaultValue);
    }

    public void write(CompoundTag root, T value) {
        if (root == null) return;
        var current = root;
        for (int i = 0; i < path.length - 1; i++) {
            var child = current.get(path[i]);
            if (child == null) {
                var compound = new CompoundTag();
                current.put(path[i], compound);
                current = compound;
            } else if (child instanceof CompoundTag compound) {
                current = compound;
            } else {
                return;
            }
        }
        var target = current;
        codec.encodeStart(NbtOps.INSTANCE, value).result().ifPresent(tag -> target.put(path[path.length - 1], tag));
    }

    public Optional<T> read(ItemStack stack) {
        return read(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
    }

    public T readOrDefault(ItemStack stack, T defaultValue) {
        return read(stack).orElse(defaultValue);
    }

    public void write(ItemStack stack, T value) {
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        write(tag, value);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static final class Builder<T> {
        private String path;
        private Codec<T> codec;

        private Builder() {}

        public Builder<T> path(String path) {
            this.path = path;
            return this;
        }

        public Builder<T> codec(Codec<T> codec) {
            this.codec = codec;
            return this;
        }

        public NBTComponent<T> build() {
            return new NBTComponent<>(path, codec);
        }
    }
}
*///? }
