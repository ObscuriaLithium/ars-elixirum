package dev.obscuria.elixirum.client.screen.widgets.style;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public interface StyleAdapter<T> {

    T[] values();

    T get(CachedElixir elixir);

    void apply(CachedElixir elixir, T value);

    SoundEvent sound(T value);

    int requiredLevel(T value);

    Component displayName(T value);

    void render(GuiGraphics graphics, int x, int y, CachedElixir elixir, T value);
}
