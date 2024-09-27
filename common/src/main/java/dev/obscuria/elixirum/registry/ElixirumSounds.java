package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public interface ElixirumSounds {
    SoundEvent ITEM_BOTTLE_PUT = register("item.bottle_put");
    SoundEvent ITEM_BOTTLE_SHAKE = register("item.bottle_shake");
    SoundEvent ITEM_BOTTLE_SLOSH = register("item.bottle_slosh");
    SoundEvent ITEM_BOTTLE_OPEN = register("item.bottle_open");
    SoundEvent BLOCK_CAULDRON_BOIL = register("block.cauldron_boil");
    SoundEvent UI_CLICK_1 = register("ui.click_1");
    SoundEvent UI_CLICK_2 = register("ui.click_2");
    SoundEvent UI_BELL = register("ui.bell");
    SoundEvent UI_SCROLL = register("ui.scroll");
    Holder<SoundEvent> MUSIC_ELIXIRUM = registerHolder("music.elixirum");

    private static SoundEvent
    register(final String name) {
        final var value = SoundEvent.createVariableRangeEvent(Elixirum.key(name));
        Elixirum.PLATFORM.registerReference(BuiltInRegistries.SOUND_EVENT, Elixirum.key(name), () -> value);
        return value;
    }

    private static Holder<SoundEvent>
    registerHolder(final String name) {
        return Elixirum.PLATFORM.registerReference(BuiltInRegistries.SOUND_EVENT, Elixirum.key(name),
                () -> SoundEvent.createVariableRangeEvent(Elixirum.key(name)));
    }

    static void setup() {}
}
