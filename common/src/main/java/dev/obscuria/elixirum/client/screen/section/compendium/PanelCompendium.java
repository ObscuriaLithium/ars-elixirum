package dev.obscuria.elixirum.client.screen.section.compendium;

import com.google.common.collect.Maps;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.tool.Property;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.container.*;
import dev.obscuria.elixirum.client.screen.widget.Text;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.util.Map;

final class PanelCompendium extends PanelContainer
{
    private static final Map<Integer, Boolean> sectionsCache;

    public PanelCompendium(int x, int y, int width, int height)
    {
        super(x, y, width, height);
        this.setHeader(new Text()
                .setContent(Component.literal("Compendium"))
                .setStyle(Elixirum.STYLE)
                .setCentered(true)
                .setScale(1.2f));
        final var split = this.setContent(new SplitContainer(0.3));
        final var navigationScroll = split.addChild(new ScrollContainer(Component.empty()));
        final var navigation = navigationScroll.addChild(new ListContainer().setSeparation(2));
        final var pageScroll = split.addChild(new ScrollContainer(Component.empty()));
        final var page = pageScroll.addChild(new ListContainer().setSeparation(8));

        addSection(1, navigation,
                Component.literal("Getting Started"),
                ContentsType.INTRODUCTION,
                ContentsType.ESSENCES_AND_INGREDIENTS,
                ContentsType.CREATING_ELIXIR);
        addSection(2, navigation,
                Component.literal("Essences"),
                ContentsType.ALL_ESSENCES,
                ContentsType.DISCOVERING_ESSENCES,
                ContentsType.AFFIXES);
        addSection(3, navigation,
                Component.literal("Extras"),
                ContentsType.CONTRIBUTION);

        RootCompendium.bind(pageScroll, page);
    }

    private static void addSection(int id, ListContainer parent, Component title, ContentsType... contentsTypes)
    {
        final var property = Property.create(
                () -> sectionsCache.getOrDefault(id, false),
                value -> sectionsCache.put(id, value));
        final var spoiler = parent.addChild(new SpoilerContainer(title).setProperty(property));
        final var list = spoiler.addChild(new ListContainer().setOffset(2, 5));
        for (var type : contentsTypes)
            list.addChild(new Contents(type)
                    .setClickSound(ElixirumSounds.UI_CLICK_2)
                    .setClickAction(ClickAction.<Contents>left(contents -> {
                        RootCompendium.select(type);
                        return true;
                    })));
    }

    static
    {
        sectionsCache = Util.make(Maps.newHashMap(), map -> map.put(1, true));
    }
}
