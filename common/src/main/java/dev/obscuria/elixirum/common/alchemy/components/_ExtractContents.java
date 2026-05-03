package dev.obscuria.elixirum.common.alchemy.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.components.ExtractContents;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolderMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record _ExtractContents(
        Item source,
        EssenceHolderMap essences
) implements ExtractContents {

    public static final ExtractContents EMPTY;
    public static final Codec<ExtractContents> CODEC;

    static {
        EMPTY = new _ExtractContents(Items.AIR, EssenceHolderMap.EMPTY);
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("source").forGetter(ExtractContents::source),
                EssenceHolderMap.CODEC.fieldOf("essences").forGetter(ExtractContents::essences)
        ).apply(codec, _ExtractContents::new));
    }
}
