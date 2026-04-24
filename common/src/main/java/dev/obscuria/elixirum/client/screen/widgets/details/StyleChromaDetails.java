package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.widgets.style.ChromaStyleAdapter;
import dev.obscuria.elixirum.client.screen.widgets.style.StylePicker;
import dev.obscuria.fragmentum.util.signal.Signal0;

public class StyleChromaDetails extends AbstractDetails {

    public StyleChromaDetails(CachedElixir elixir, Signal0 changed) {
        super(ElixirumUI.STYLE_CHROMA);
        this.addChild(new StylePicker<>(ChromaStyleAdapter.SHARED, elixir, changed));
    }
}
