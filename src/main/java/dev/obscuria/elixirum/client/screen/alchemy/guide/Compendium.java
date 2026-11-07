package dev.obscuria.elixirum.client.screen.alchemy.guide;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.alchemy.guide.contents.ContentBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Compendium(
        Index index,
        Map<String, Page> pageByKey
) {

    public Page findHomepage() {
        return findPage(index.homepage());
    }

    public Page findPage(String key) {
        return pageByKey.getOrDefault(key, Page.EMPTY);
    }

    public record Index(
            String title,
            String subtitle,
            String homepage,
            List<Section> sections
    ) {

        public MutableComponent titleComponent() {
            return Component.literal(title);
        }

        public MutableComponent subtitleComponent() {
            return Component.literal(subtitle);
        }

        public static final Codec<Index> CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.STRING.fieldOf("title").forGetter(Index::title),
                Codec.STRING.fieldOf("subtitle").forGetter(Index::subtitle),
                Codec.STRING.fieldOf("homepage").forGetter(Index::homepage),
                Section.CODEC.listOf().fieldOf("sections").forGetter(Index::sections)
        ).apply(codec, Index::new));
    }

    public record Section(
            String title,
            List<String> pages
    ) {

        public MutableComponent titleComponent() {
            return Component.literal(title);
        }

        public static final Codec<Section> CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.STRING.fieldOf("title").forGetter(Section::title),
                Codec.STRING.listOf().fieldOf("pages").forGetter(Section::pages)
        ).apply(codec, Section::new));
    }

    public record Page(
            boolean isHomepage,
            Optional<Item> icon,
            String title,
            List<ContentBlock> contents
    ) {

        public boolean isEmpty() {
            return contents.isEmpty();
        }

        public static final Page EMPTY = new Page(false, Optional.empty(), "Unknown", List.of());
        public static final Codec<Page> CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Codec.BOOL.optionalFieldOf("is_homepage", false).forGetter(Page::isHomepage),
                BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("icon").forGetter(Page::icon),
                Codec.STRING.fieldOf("title").forGetter(Page::title),
                ContentBlock.CODEC.listOf().fieldOf("contents").forGetter(Page::contents)
        ).apply(codec, Page::new));
    }
}
