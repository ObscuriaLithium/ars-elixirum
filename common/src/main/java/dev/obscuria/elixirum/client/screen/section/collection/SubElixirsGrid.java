package dev.obscuria.elixirum.client.screen.section.collection;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.container.GridContainer;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.widget.AbstractElixirDisplay;
import dev.obscuria.elixirum.registry.ElixirumSounds;

final class SubElixirsGrid extends GridContainer {

    public SubElixirsGrid() {
        for (var holder : ClientAlchemy.getProfile().getSavedPages()) {
            final var getter = RootCollection.essenceGetter();
            this.addChild(new Entry(new RootCollection.Elixir(holder, holder.createStack(getter)))
                    .setClickSound(ElixirumSounds.UI_CLICK_2.holder())
                    .setClickAction(ClickAction.<Entry>left(widget -> {
                        RootCollection.select(widget.getElixir());
                        return true;
                    })));
        }
    }

    static final class Entry extends AbstractElixirDisplay {
        private final RootCollection.Elixir elixir;

        public Entry(RootCollection.Elixir elixir) {
            super(elixir.stack());
            this.elixir = elixir;
        }

        public RootCollection.Elixir getElixir() {
            return this.elixir;
        }

        @Override
        protected boolean isSelected() {
            return RootCollection.getSelected().map(elixir -> elixir == this.elixir).orElse(false);
        }
    }
}
