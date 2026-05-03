package dev.obscuria.elixirum.client.screen.alchemy.details.style;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public final class ChromaStyleAdapter implements StyleAdapter<Chroma> {

    public static final ChromaStyleAdapter SHARED = new ChromaStyleAdapter();

    private ChromaStyleAdapter() {}

    @Override
    public Chroma[] values() {
        return Chroma.values();
    }

    @Override
    public Chroma get(CachedElixir elixir) {
        return elixir.configured().getChroma();
    }

    @Override
    public void apply(CachedElixir elixir, Chroma value) {
        elixir.setChromaSynced(value);
    }

    @Override
    public SoundEvent sound(Chroma value) {
        return ElixirumSounds.UI_BOTTLE_DYE;
    }

    @Override
    public int requiredLevel(Chroma value) {
        return value.mastery;
    }

    @Override
    public Component displayName(Chroma value) {
        return value.displayName();
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int x, int y, CachedElixir elixir, Chroma value) {
        context.pushModulate(value.computeColor(ArsElixirumAPI.getElixirContents(elixir.get())));
        GuiToolkit.draw(graphics, Textures.CHROMA, x + 2, y + 2, 12, 12);
        context.popModulate();
    }
}
