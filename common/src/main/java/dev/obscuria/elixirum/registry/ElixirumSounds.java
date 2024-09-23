package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public interface ElixirumSounds {
    LazyRegister<SoundEvent> SOURCE = LazyRegister.create(BuiltInRegistries.SOUND_EVENT, Elixirum.MODID);

    LazyValue<SoundEvent, SoundEvent> UI_CLICK_1 = simple("ui.click_1");
    LazyValue<SoundEvent, SoundEvent> UI_CLICK_2 = simple("ui.click_2");
    LazyValue<SoundEvent, SoundEvent> UI_BELL = simple("ui.bell");
    LazyValue<SoundEvent, SoundEvent> UI_SCROLL = simple("ui.scroll");
    LazyValue<SoundEvent, SoundEvent> MUSIC_ELIXIRUM = simple("music.elixirum");

    private static LazyValue<SoundEvent, SoundEvent>
    simple(final String name) {
        return SOURCE.register(name, () -> SoundEvent.createVariableRangeEvent(Elixirum.key(name)));
    }
}
