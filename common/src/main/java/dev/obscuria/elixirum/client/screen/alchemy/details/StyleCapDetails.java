package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.details.style.CapStyleAdapter;
import dev.obscuria.elixirum.client.screen.alchemy.details.style.StylePicker;
import dev.obscuria.fragmentum.util.signal.Signal0;

public class StyleCapDetails extends AbstractDetails {

    public StyleCapDetails(CachedElixir elixir, Signal0 changed) {
        super(ElixirumUI.STYLE_CAP);
        this.addChild(new StylePicker<>(CapStyleAdapter.SHARED, elixir, changed));
    }
}
