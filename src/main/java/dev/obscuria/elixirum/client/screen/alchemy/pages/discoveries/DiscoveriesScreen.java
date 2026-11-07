package dev.obscuria.elixirum.client.screen.alchemy.pages.discoveries;

import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyPage;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.alchemy.MasteryProgress;

public class DiscoveriesScreen extends AlchemyScreen {

    public DiscoveriesScreen() {
        super(AlchemyPage.DISCOVERIES);
    }

    @Override
    protected void init() {
        super.init();
        var selection = new Selection<>(DiscoveryTarget.EMPTY);
        this.addChild(new MasteryProgress(center(), 0));
        this.addChild(new DiscoveriesEffects(selection, left(10), top(10), 126, height - 20));
        this.addChild(new DiscoveriesDetails(selection, right(-134), top(10), 126, height - 20));
    }
}
