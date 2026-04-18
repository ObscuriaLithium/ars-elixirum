package dev.obscuria.elixirum.client.screen.widgets.pages.collection;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.widgets.AbstractElixirWidget;

class CollectionElixirWidget extends AbstractElixirWidget {

    protected CollectionElixirWidget(SelectionState<CachedElixir> selection, CachedElixir elixir) {
        super(selection, elixir);
    }
}