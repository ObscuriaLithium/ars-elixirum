package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.controls.layout.VSpacingControl;

public record SpacingBlock(int height) implements ContentBlock {

    public static final Codec<SpacingBlock> CODEC;

    @Override
    public Codec<SpacingBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        return new VSpacingControl(height);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.INT.fieldOf("height").forGetter(SpacingBlock::height)
        ).apply(codec, SpacingBlock::new));
    }
}
