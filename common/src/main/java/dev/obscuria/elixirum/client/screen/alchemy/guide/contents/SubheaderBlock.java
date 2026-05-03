package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.SubheaderControl;
import net.minecraft.network.chat.Component;

public record SubheaderBlock(String text) implements ContentBlock {

    public static final Codec<SubheaderBlock> CODEC;

    @Override
    public Codec<SubheaderBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        var subheader = new SubheaderControl(Component.literal(text));
        subheader.setScale(1.1f);
        subheader.setCentered(false);
        return subheader;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.STRING.fieldOf("text").forGetter(SubheaderBlock::text)
        ).apply(codec, SubheaderBlock::new));
    }
}
