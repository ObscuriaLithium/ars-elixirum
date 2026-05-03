package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.alchemy.controls.AspectFitImageControl;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import net.minecraft.resources.ResourceLocation;

public record ImageBlock(
        ResourceLocation texture,
        int textureWidth,
        int textureHeight,
        int height
) implements ContentBlock {

    public static final Codec<ImageBlock> CODEC;

    @Override
    public Codec<ImageBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        return new AspectFitImageControl(texture, textureWidth, textureHeight, height);
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(ImageBlock::texture),
                Codec.INT.fieldOf("texture_width").forGetter(ImageBlock::textureWidth),
                Codec.INT.fieldOf("texture_height").forGetter(ImageBlock::textureHeight),
                Codec.INT.fieldOf("height").forGetter(ImageBlock::height)
        ).apply(codec, ImageBlock::new));
    }
}
