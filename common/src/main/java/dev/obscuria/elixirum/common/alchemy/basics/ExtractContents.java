package dev.obscuria.elixirum.common.alchemy.basics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public record ExtractContents(
        Item source,
        EssenceHolderMap essences
) implements EssenceProvider {

    public static final ExtractContents EMPTY;
    public static final Codec<ExtractContents> CODEC;

    public boolean isEmpty() {
        return essences.isEmpty();
    }

    @Override
    public Stream<Object2IntMap.Entry<EssenceHolder>> streamSorted() {
        return essences.streamSorted();
    }

    @Override
    public void forEachSorted(BiConsumer<EssenceHolder, Integer> consumer) {
        this.essences.forEachSorted(consumer);
    }

    static {
        EMPTY = new ExtractContents(Items.AIR, EssenceHolderMap.EMPTY);
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("source").forGetter(ExtractContents::source),
                EssenceHolderMap.CODEC.fieldOf("essences").forGetter(ExtractContents::essences)
        ).apply(codec, ExtractContents::new));
    }
}
