package dev.obscuria.elixirum.client.screen.section.collection;

import dev.obscuria.elixirum.client.screen.container.ListContainer;
import dev.obscuria.elixirum.client.screen.widget.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widget.ElixirOverview;
import dev.obscuria.elixirum.client.screen.widget.Spacing;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;

final class PanelDetails extends AbstractDetailsPanel {

    public PanelDetails(ElixirOverview overview, int x, int y, int width, int height) {
        super(overview, x, y, width, height);
        RootCollection.setSelectionListener(holder -> update(holder.orElseGet(ElixirHolder::empty)));
    }

    @Override
    protected void buildContents(ListContainer container) {
        super.buildContents(container);
        final var removeButton = new SubRemoveButton();
        this.listeners.add(holder -> removeButton.resetConfirm());
        container.addChild(new Spacing(0, 10));
        container.addChild(removeButton);
    }
}
