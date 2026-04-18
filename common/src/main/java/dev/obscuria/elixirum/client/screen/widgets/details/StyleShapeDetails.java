package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.widgets.StylePicker;
import dev.obscuria.fragmentum.util.signal.Signal0;
import net.minecraft.network.chat.Component;

public class StyleShapeDetails extends AbstractDetails {

    public StyleShapeDetails(Signal0 updated, CachedElixir elixir) {
        super(Component.literal("Shape"));
        this.addChild(new StylePicker.OfShape(updated, elixir));
    }
}
