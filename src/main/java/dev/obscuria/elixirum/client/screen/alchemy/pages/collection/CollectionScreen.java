package dev.obscuria.elixirum.client.screen.alchemy.pages.collection;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyPage;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.alchemy.ElixirOverview;
import dev.obscuria.elixirum.client.screen.alchemy.MasteryProgress;
import dev.obscuria.fragmentum.util.signal.Signal0;

public class CollectionScreen extends AlchemyScreen {

    public CollectionScreen() {
        super(AlchemyPage.COLLECTION);
    }

    @Override
    protected void init() {
        super.init();
        var selection = new Selection<>(CachedElixir.empty());
        var styleChanged = new Signal0();
        this.addChild(new MasteryProgress(center(), 0));
        this.addChild(new ElixirOverview(selection, styleChanged, center(), height));
        this.addChild(new CollectionElixirs(selection, left(10), top(10), 126, height - 20));
        this.addChild(new CollectionDetails(selection, styleChanged, right(-134), top(10), 126, height - 20));
    }
}
