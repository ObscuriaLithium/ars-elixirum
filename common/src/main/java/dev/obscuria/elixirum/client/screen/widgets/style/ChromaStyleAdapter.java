package dev.obscuria.elixirum.client.screen.widgets.style;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import dev.obscuria.elixirum.helpers.ContentsHelper;
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
    public void render(GuiGraphics graphics, int x, int y, CachedElixir elixir, Chroma value) {
        GuiGraphicsUtil.setShaderColor(value.computeColor(ContentsHelper.elixir(elixir.get())));
        GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.CHROMA, x + 2, y + 2, 12, 12);
        GuiGraphicsUtil.resetShaderColor();
    }
}
