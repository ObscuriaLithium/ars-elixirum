package dev.obscuria.elixirum.client.screen.section.collection;

import dev.obscuria.elixirum.client.screen.widget.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widget.ElixirOverview;

final class PanelDetails extends AbstractDetailsPanel {

    public PanelDetails(ElixirOverview overview, int x, int y, int width, int height) {
        super(overview, x, y, width, height);
        RootCollection.updateListener = elixir -> update(elixir.stack(), elixir.holder().recipe());
    }
}
