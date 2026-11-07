package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.client.registry.ElixirumClientRegistries;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.fragmentum.registry.BootstrapContext;

import java.util.function.Function;

public interface ContentBlock {

    Codec<ContentBlock> CODEC = ElixirumClientRegistries
            .CONTENT_BLOCK_TYPE.byNameCodec()
            .dispatch(ContentBlock::codec, Function.identity());

    Codec<? extends ContentBlock> codec();

    Control instantiate();

    static void bootstrap(BootstrapContext<Codec<? extends ContentBlock>> context) {
        context.register("blank", () -> BlankBlock.CODEC);
        context.register("image", () -> ImageBlock.CODEC);
        context.register("header", () -> HeaderBlock.CODEC);
        context.register("subheader", () -> SubheaderBlock.CODEC);
        context.register("paragraph", () -> ParagraphBlock.CODEC);
        context.register("item_panel", () -> ItemPanelBlock.CODEC);
        context.register("table", () -> TableBlock.CODEC);
        context.register("spacing", () -> SpacingBlock.CODEC);
    }
}
