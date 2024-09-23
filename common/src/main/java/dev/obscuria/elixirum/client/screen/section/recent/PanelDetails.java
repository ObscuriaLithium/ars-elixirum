package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.widget.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widget.ElixirOverview;

import java.util.Optional;

final class PanelDetails extends AbstractDetailsPanel {

    public PanelDetails(ElixirOverview overview, int x, int y, int width, int height) {
        super(overview, x, y, width, height);
        RootRecent.updateListener = elixir -> update(elixir.stack(), elixir.recipe());
    }

    @Override
    protected Optional<HierarchicalWidget> createFooter() {
        return Optional.of(new SubSaveButton());
    }
}
