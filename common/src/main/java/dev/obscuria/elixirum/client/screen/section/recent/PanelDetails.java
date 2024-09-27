package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.widget.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widget.ElixirOverview;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;

import java.util.Optional;

final class PanelDetails extends AbstractDetailsPanel {

    public PanelDetails(ElixirOverview overview, int x, int y, int width, int height) {
        super(overview, x, y, width, height);
        RootRecent.setSelectionListener(holder -> update(holder.orElseGet(ElixirHolder::empty)));
    }

    @Override
    protected Optional<HierarchicalWidget> createFooter() {
        return Optional.of(new SubSaveButton());
    }
}
