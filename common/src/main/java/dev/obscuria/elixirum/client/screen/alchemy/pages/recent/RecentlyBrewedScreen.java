package dev.obscuria.elixirum.client.screen.alchemy.pages.recent;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyPage;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.alchemy.ElixirOverview;
import dev.obscuria.elixirum.client.screen.alchemy.MasteryProgress;

public final class RecentlyBrewedScreen extends AlchemyScreen {

    public RecentlyBrewedScreen() {
        super(AlchemyPage.RECENTLY_BREWED);
    }

    @Override
    protected void init() {
        super.init();
        var selection = new Selection<>(CachedElixir.empty());
        this.addChild(new MasteryProgress(center(), 0));
        this.addChild(new ElixirOverview(selection, center(), height));
        this.addChild(new RecentlyBrewedElixirs(selection, left(10), top(10), 126, height - 20));
        this.addChild(new RecentlyBrewedDetails(selection, right(-134), top(10), 126, height - 20));
    }
}
