package dev.obscuria.elixirum.helpers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public final class CodecHelper {

    public static final Codec<Item> STRICT_ITEM;

    public static <T> Codec<T> alternative(Codec<T> first, Codec<T> second) {
        return Codec.either(first, second).xmap(CodecHelper::unwrap, Either::left);
    }

    public static <T> Codec<AtomicReference<T>> atomic(Codec<T> codec) {
        return codec.xmap(AtomicReference::new, AtomicReference::get);
    }

    public static <T> MapCodec<AtomicReference<T>> atomic(MapCodec<T> codec) {
        return codec.xmap(AtomicReference::new, AtomicReference::get);
    }

    public static MapCodec<AtomicInteger> atomicInt(MapCodec<Integer> codec) {
        return codec.xmap(AtomicInteger::new, AtomicInteger::get);
    }

    private static <T> T unwrap(Either<T, T> either) {
        return either.map(Function.identity(), Function.identity());
    }

    private static DataResult<Item> itemByKey(ResourceLocation key) {
        var item = BuiltInRegistries.ITEM.get(key);
        return item != Items.AIR
                ? DataResult.success(item)
                : DataResult.error(() -> "Unknown item '%s'".formatted(key));
    }

    static {
        STRICT_ITEM = ResourceLocation.CODEC.comapFlatMap(
                CodecHelper::itemByKey,
                BuiltInRegistries.ITEM::getKey);
    }
}
