package dev.obscuria.elixirum.client.screen.widgets.style;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.common.alchemy.styles.Shape;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public final class ShapeStyleAdapter implements StyleAdapter<Shape> {

    public static final ShapeStyleAdapter SHARED = new ShapeStyleAdapter();

    private ShapeStyleAdapter() {}

    @Override
    public Shape[] values() {
        return Shape.values();
    }

    @Override
    public Shape get(CachedElixir elixir) {
        return elixir.configured().getStyle().shape();
    }

    @Override
    public void apply(CachedElixir elixir, Shape value) {
        elixir.mapStyleSynced(style -> style.withShape(value));
    }

    @Override
    public SoundEvent sound(Shape value) {
        return ElixirumSounds.UI_BOTTLE_CLINK;
    }

    @Override
    public int requiredLevel(Shape value) {
        return value.mastery;
    }

    @Override
    public Component displayName(Shape value) {
        return value.displayName();
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, CachedElixir elixir, Shape value) {
        var texture = ArsElixirum.identifier("textures/item/elixir/" + value.texture + ".png");
        graphics.blit(texture, x - 1, y - 3, 0f, 0f, 16, 16, 16, 16);
    }
}
