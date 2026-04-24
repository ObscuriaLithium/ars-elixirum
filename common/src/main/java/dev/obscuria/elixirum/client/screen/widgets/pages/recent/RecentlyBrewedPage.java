package dev.obscuria.elixirum.client.screen.widgets.pages.recent;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.widgets.ElixirOverview;
import dev.obscuria.elixirum.client.screen.widgets.MasteryProgress;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.client.screen.widgets.pages.PageKind;

public class RecentlyBrewedPage extends AbstractPage {

    public RecentlyBrewedPage() {
        super(PageKind.RECENTLY_BREWED);
    }

    @Override
    protected void init() {
        super.init();
        var selection = new SelectionState<>(CachedElixir.empty());
        this.addChild(new MasteryProgress(center(), 0));
        this.addChild(new ElixirOverview(selection, center(), height));
        this.addChild(new RecentPanelRecipes(selection, left(10), top(10), 126, height - 20));
        this.addChild(new RecentPanelDetails(selection, right(-134), top(10), 126, height - 20));
    }
}
