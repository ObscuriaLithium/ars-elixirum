package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.widgets.style.ShapeStyleAdapter;
import dev.obscuria.elixirum.client.screen.widgets.style.StylePicker;
import dev.obscuria.fragmentum.util.signal.Signal0;

public class StyleShapeDetails extends AbstractDetails {

    public StyleShapeDetails(CachedElixir elixir, Signal0 changed) {
        super(ElixirumUI.STYLE_SHAPE);
        this.addChild(new StylePicker<>(ShapeStyleAdapter.SHARED, elixir, changed));
    }
}
