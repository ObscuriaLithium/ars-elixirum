package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.alchemy.controls.ItemPanelControl;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.List;

public record ItemPanelBlock(Item item, List<ContentBlock> contents) implements ContentBlock {

    public static final Codec<ItemPanelBlock> CODEC;

    @Override
    public Codec<ItemPanelBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        var panel = new ItemPanelControl(item.getDefaultInstance());
        var contents = panel.add(ListContainer.createBuilder().separation(10).build());
        this.contents.stream().map(ContentBlock::instantiate).forEach(contents::addChild);
        return panel;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemPanelBlock::item),
                ContentBlock.CODEC.listOf().fieldOf("contents").forGetter(ItemPanelBlock::contents)
        ).apply(codec, ItemPanelBlock::new));
    }
}
