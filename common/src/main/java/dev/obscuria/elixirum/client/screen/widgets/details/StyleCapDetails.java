package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.widgets.style.CapStyleAdapter;
import dev.obscuria.elixirum.client.screen.widgets.style.StylePicker;
import dev.obscuria.fragmentum.util.signal.Signal0;

public class StyleCapDetails extends AbstractDetails {

    public StyleCapDetails(CachedElixir elixir, Signal0 changed) {
        super(ElixirumUI.STYLE_CAP);
        this.addChild(new StylePicker<>(CapStyleAdapter.SHARED, elixir, changed));
    }
}
