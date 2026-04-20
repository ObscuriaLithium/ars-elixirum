package dev.obscuria.elixirum.client.screen.widgets.pages.discoveries;

import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.widgets.MasteryProgress;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.client.screen.widgets.pages.PageKind;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;

public class DiscoveriesPage extends AbstractPage {

    public DiscoveriesPage() {
        super(PageKind.DISCOVERIES);
    }

    @Override
    protected void init() {
        super.init();
        var selection = new SelectionState<>(Essence.EMPTY);
        this.addChild(new MasteryProgress(center(), 0));
        this.addChild(new DiscoveriesPanelEffects(selection, left(10), top(10), 126, height - 20));
        this.addChild(new DiscoveriesPanelDetails(selection, right(-134), top(10), 126, height - 20));
    }
}
