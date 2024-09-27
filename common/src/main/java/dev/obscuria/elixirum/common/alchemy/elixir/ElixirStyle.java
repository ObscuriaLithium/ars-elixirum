package dev.obscuria.elixirum.common.alchemy.elixir;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.common.alchemy.style.Cap;
import dev.obscuria.elixirum.common.alchemy.style.Shape;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ElixirStyle(Shape shape, Cap cap) {
    public static final Codec<ElixirStyle> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ElixirStyle> STREAM_CODEC;
    public static final ElixirStyle DEFAULT = new ElixirStyle(Shape.DEFAULT, Cap.DEFAULT);

    public static ElixirStyle create(int shapeId, int capId) {
        return new ElixirStyle(
                Shape.getById(shapeId),
                Cap.getById(capId));
    }

    public static ElixirStyle get(ItemStack stack) {
        return stack.getOrDefault(ElixirumDataComponents.ELIXIR_STYLE, DEFAULT);
    }

    public static Shape getShape(ItemStack stack) {
        return stack.getOrDefault(ElixirumDataComponents.ELIXIR_STYLE, DEFAULT).shape;
    }

    public static Cap getCap(ItemStack stack) {
        return stack.getOrDefault(ElixirumDataComponents.ELIXIR_STYLE, DEFAULT).cap;
    }

    public static float getShapePredicate(ItemStack stack,
                                          @Nullable Level level,
                                          @Nullable LivingEntity entity,
                                          int seed) {
        return (float) ElixirStyle.getShape(stack).getPredicate();
    }

    public static float getCapPredicate(ItemStack stack,
                                          @Nullable Level level,
                                          @Nullable LivingEntity entity,
                                          int seed) {
        return (float) ElixirStyle.getCap(stack).getPredicate();
    }

    public ElixirStyle withShape(Shape shape) {
        return new ElixirStyle(shape, cap);
    }

    public ElixirStyle withCap(Cap cap) {
        return new ElixirStyle(shape, cap);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("shape").forGetter(style -> style.shape.getId()),
                Codec.INT.fieldOf("cap").forGetter(style -> style.cap.getId())
        ).apply(instance, ElixirStyle::create));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, style -> style.shape.getId(),
                ByteBufCodecs.INT, style -> style.cap.getId(),
                ElixirStyle::create);
    }
}
