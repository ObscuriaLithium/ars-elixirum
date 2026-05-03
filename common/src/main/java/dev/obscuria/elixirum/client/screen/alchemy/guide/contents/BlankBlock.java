package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.controls.layout.BlankControl;

public record BlankBlock() implements ContentBlock {

    public static final BlankBlock SHARED = new BlankBlock();
    public static final Codec<BlankBlock> CODEC = Codec.unit(SHARED);

    @Override
    public Codec<BlankBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        return new BlankControl();
    }
}
