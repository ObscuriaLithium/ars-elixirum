package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.fragmentum.registry.Deferred;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public interface ElixirumSounds {

    SoundEvent UI_BELL = register("ui.bell");
    SoundEvent UI_CLICK_1 = register("ui.click_1");
    SoundEvent UI_CLICK_2 = register("ui.click_2");
    SoundEvent UI_SCROLL = register("ui.scroll");
    SoundEvent UI_BOTTLE_CLINK = register("ui.bottle_clink");
    SoundEvent UI_BOTTLE_OPEN = register("ui.bottle_open");
    SoundEvent UI_BOTTLE_DYE = register("ui.bottle_dye");
    SoundEvent UI_MASTERY_LEVEL_UP = register("ui.mastery.level_up");

    SoundEvent BLOCK_CAULDRON_BOIL = register("block.cauldron.boil");

    SoundEvent ITEM_BOTTLE_PUT = register("item.bottle.put");
    SoundEvent ITEM_BOTTLE_SHAKE = register("item.bottle.shake");
    SoundEvent ITEM_BOTTLE_SLOSH = register("item.bottle.slosh");

    SoundEvent RECORD_WUNSCHPUNSCH = register("record.wunschpunsch");

    Deferred<SoundEvent, SoundEvent> MUSIC_ELIXIRUM = registerForHolder("music.elixirum");

    private static SoundEvent register(String name) {
        final var sound = SoundEvent.createVariableRangeEvent(ArsElixirum.identifier(name));
        ElixirumRegistries.REGISTRAR.register(Registries.SOUND_EVENT, ArsElixirum.identifier(name), () -> sound);
        return sound;
    }

    private static Deferred<SoundEvent, SoundEvent> registerForHolder(String name) {
        final var sound = SoundEvent.createVariableRangeEvent(ArsElixirum.identifier(name));
        return ElixirumRegistries.REGISTRAR.register(Registries.SOUND_EVENT, ArsElixirum.identifier(name), () -> sound);
    }

    static void init() {}
}
