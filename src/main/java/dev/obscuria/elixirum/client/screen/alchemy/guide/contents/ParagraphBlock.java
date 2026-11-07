package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.ParagraphControl;
import dev.obscuria.fragmentum.world.tooltip.TooltipOptions;
import net.minecraft.network.chat.Component;

public record ParagraphBlock(String text) implements ContentBlock {

    public static final Codec<ParagraphBlock> CODEC;
    public static final TooltipOptions OPTIONS;

    @Override
    public Codec<ParagraphBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        var paragraph = new ParagraphControl(GuiToolkit.format(Component.literal(text)));
        paragraph.setScale(0.75f);
        return paragraph;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.STRING.fieldOf("text").forGetter(ParagraphBlock::text)
        ).apply(codec, ParagraphBlock::new));
        OPTIONS = TooltipOptions.builder()
                .withMaxLineLength(Integer.MAX_VALUE)
                .build();
    }
}
