package dev.obscuria.elixirum.client.screen.alchemy.details.style;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.common.alchemy.styles.Cap;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public final class CapStyleAdapter implements StyleAdapter<Cap> {

    public static final CapStyleAdapter SHARED = new CapStyleAdapter();

    private CapStyleAdapter() {}

    @Override
    public Cap[] values() {
        return Cap.values();
    }

    @Override
    public Cap get(CachedElixir elixir) {
        return elixir.configured().getStyle().cap();
    }

    @Override
    public void apply(CachedElixir elixir, Cap value) {
        elixir.mapStyleSynced(style -> style.withCap(value));
    }

    @Override
    public SoundEvent sound(Cap value) {
        return ElixirumSounds.UI_BOTTLE_OPEN;
    }

    @Override
    public int requiredLevel(Cap value) {
        return value.mastery;
    }

    @Override
    public Component displayName(Cap value) {
        return value.displayName();
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int x, int y, CachedElixir elixir, Cap value) {
        var texture = ArsElixirum.identifier("textures/item/elixir/" + value.texture + ".png");
        graphics.pose().pushPose();
        graphics.pose().translate(x + 6.75f, y + 7.5f, 0f);
        graphics.pose().scale(1.5f, 1.5f, 1.5f);
        graphics.blit(texture, -8, -3, 0f, 0f, 16, 16, 16, 16);
        graphics.pose().popPose();
    }
}
