package dev.obscuria.elixirum.client.screen.widgets.pages.recent;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.widgets.ElixirOverview;
import dev.obscuria.elixirum.client.screen.widgets.MasteryProgress;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.client.screen.widgets.pages.PageKind;
import dev.obscuria.fragmentum.util.signal.Signal0;

public class RecentPage extends AbstractPage {

    public RecentPage() {
        super(PageKind.RECENT);
    }

    @Override
    protected void init() {
        super.init();
        var selection = new SelectionState<>(CachedElixir.empty());
        this.addChild(new MasteryProgress(center(), 0));
        this.addChild(new ElixirOverview(selection, new Signal0(), center(), height));
        this.addChild(new RecentPanelRecipes(selection, left(10), top(10), 126, height - 20));
        this.addChild(new RecentPanelDetails(selection, right(-134), top(10), 126, height - 20));
    }
}
