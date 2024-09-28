package dev.obscuria.elixirum.common.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ExtractContents(Optional<Item> source,
                              Holder<Essence> essenceHolder,
                              int weight) {

    public static final Codec<ExtractContents> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtractContents> STREAM_CODEC;

    public static Optional<ExtractContents> get(ItemStack stack) {
        return Optional.ofNullable(stack.get(ElixirumDataComponents.EXTRACT_CONTENTS));
    }

    public static int getOverlayColor(ItemStack stack, int layer) {
        return layer != 1 ? -1 : get(stack)
                .map(contents -> FastColor.ARGB32.opaque(contents.getEssence().getEffect().getColor()))
                .orElse(Elixirum.WATER_COLOR);
    }

    public Essence getEssence() {
        return essenceHolder.value();
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("source").forGetter(ExtractContents::source),
                Essence.CODEC.fieldOf("essence").forGetter(ExtractContents::essenceHolder),
                Codec.INT.fieldOf("weight").forGetter(ExtractContents::weight)
        ).apply(instance, ExtractContents::new));
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ITEM)), ExtractContents::source,
                Essence.STREAM_CODEC, ExtractContents::essenceHolder,
                ByteBufCodecs.INT, ExtractContents::weight,
                ExtractContents::new);
    }
}
