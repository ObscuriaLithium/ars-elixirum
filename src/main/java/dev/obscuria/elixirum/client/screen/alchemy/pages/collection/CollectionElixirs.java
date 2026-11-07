package dev.obscuria.elixirum.client.screen.alchemy.pages.collection;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.AlchemyCache;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.FootnoteControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PageContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.HeaderControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.SearchControl;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

class CollectionElixirs extends PageContainer {

    private static final Component HEADER_TEXT;
    private static final Component BODY_TEXT;
    private static final Component FOOTER_TEXT;

    private final List<CollectionElixirControl> elixirs = new ArrayList<>();

    protected CollectionElixirs(Selection<CachedElixir> selection, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setHeader(new HeaderControl(HEADER_TEXT));
        this.setBody(Util.make(new ScrollContainer(BODY_TEXT), scroll -> {
            var list = ListContainer.createBuilder().separation(1).build();
            var container = new GridContainer(1);
            ClientAlchemy.localProfile().recipeCollection().streamOrdered().forEach(configured -> {
                var elixir = AlchemyCache.cachedElixirOf(configured.recipe());
                var widget = new CollectionElixirControl(selection, elixir);
                this.elixirs.add(widget);
                widget.makeClickHandler(selection::set);
                container.addChild(widget);
            });
            list.addChild(container);
            scroll.addChild(list);
        }));
        this.setFooter(Util.make(ListContainer.createBuilder().separation(4).build(), header -> {
            header.addChild(new FootnoteControl(FOOTER_TEXT));
            var search = header.add(SearchControl.withHandler(Component.literal("Search..."), this::applyFilter));
            search.setScale(0.75f);
        }));
    }

    private void applyFilter(String filter) {
        for (var control : elixirs) {
            control.applyFilter(filter);
        }
    }

    static {
        HEADER_TEXT = Component.translatable("ui.elixirum.collection.header");
        BODY_TEXT = Component.translatable("ui.elixirum.collection.body");
        FOOTER_TEXT = Component.translatable("ui.elixirum.collection.footer");
    }
}