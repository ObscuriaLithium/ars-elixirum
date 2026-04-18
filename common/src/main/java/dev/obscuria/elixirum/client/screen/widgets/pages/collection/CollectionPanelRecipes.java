package dev.obscuria.elixirum.client.screen.widgets.pages.collection;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.AlchemyCache;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PanelContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HeaderControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

class CollectionPanelRecipes extends PanelContainer {

    private static final Component TITLE;
    private static final Component PLACEHOLDER;

    private final HeaderControl header;
    private final ScrollContainer scroll;

    protected CollectionPanelRecipes(SelectionState<CachedElixir> selection, int x, int y, int width, int height) {
        super(x, y, width, height);

        this.header = setHeader(new HeaderControl(TITLE));
        this.scroll = setBody(Util.make(new ScrollContainer(PLACEHOLDER), scroll -> {
            final var list = new ListContainer(0, 1, 0);
            final var container = new GridContainer(1);
            for (var configured : ClientAlchemy.INSTANCE.localProfile().collection().recipes()) {
                var elixir = AlchemyCache.cachedElixirOf(configured.recipe());
                var widget = new CollectionElixirWidget(selection, elixir);
                widget.makeStackClickAction(selection::set);
                container.addChild(widget);
            }
            list.addChild(container);
            scroll.addChild(list);
        }));
        this.setFooter(ParagraphControl.panelFooter(Component.literal("Collection of saved recipes with customization options.")));
    }

    static {
        TITLE = Component.translatable("elixirum.screen.collection.recipes.title");
        PLACEHOLDER = Component.translatable("elixirum.screen.collection.recipes.placeholder");
    }
}