package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.HeaderControl;
import net.minecraft.network.chat.Component;

public record HeaderBlock(String text) implements ContentBlock {

    public static final Codec<HeaderBlock> CODEC;

    @Override
    public Codec<HeaderBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        var list = ListContainer.createBuilder().paddingVertical(6).build();
        var subheader = list.add(new HeaderControl(Component.literal(text)));
        subheader.setScale(1.5f);
        return list;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.STRING.fieldOf("text").forGetter(HeaderBlock::text)
        ).apply(codec, HeaderBlock::new));
    }
}
