package dev.obscuria.elixirum.client.screen.widgets.pages.collection;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.widgets.ElixirOverview;
import dev.obscuria.elixirum.client.screen.widgets.MasteryProgress;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.client.screen.widgets.pages.PageKind;
import dev.obscuria.fragmentum.util.signal.Signal0;

public class CollectionPage extends AbstractPage {

    public CollectionPage() {
        super(PageKind.COLLECTION);
    }

    @Override
    protected void init() {
        super.init();
        var selection = new SelectionState<>(CachedElixir.empty());
        var styleUpdated = new Signal0();
        this.addChild(new MasteryProgress(center(), 0));
        this.addChild(new ElixirOverview(selection, styleUpdated, center(), height));
        this.addChild(new CollectionPanelRecipes(selection, left(10), top(10), 126, height - 20));
        this.addChild(new CollectionPanelDetails(selection, styleUpdated, right(-134), top(10), 126, height - 20));
    }
}
