package dev.obscuria.elixirum.client.screen.alchemy.pages.collection;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.alchemy.controls.ElixirControl;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import net.minecraft.client.gui.GuiGraphics;

class CollectionElixirControl extends ElixirControl {

    private boolean highlighted;

    protected CollectionElixirControl(Selection<CachedElixir> selection, CachedElixir elixir) {
        super(selection, elixir);
    }

    public void applyFilter(String filter) {

        if (filter.isBlank()) {
            this.highlighted = false;
            return;
        }

        filter = filter.toLowerCase();

        if (elixir.displayName().getString().toLowerCase().contains(filter)) {
            this.highlighted = true;
            return;
        }

        for (var effect : elixir.contents().effects()) {
            if (effect.isVoided()) continue;

            var name = effect.displayName().getString();
            if (name.toLowerCase().contains(filter)) {
                this.highlighted = true;
                return;
            }
        }

        this.highlighted = false;
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (highlighted) this.highlight = 1f;
        super.render(graphics, context, mouseX, mouseY);
    }
}