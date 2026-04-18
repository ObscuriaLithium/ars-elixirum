package dev.obscuria.elixirum.client.sounds;

import com.google.common.collect.Lists;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class CauldronSoundInstance extends AbstractTickableSoundInstance {

    private static final List<CauldronSoundInstance> TRACKED_SOUNDS = Lists.newArrayList();
    private final GlassCauldronEntity entity;

    public CauldronSoundInstance(GlassCauldronEntity entity) {
        super(ElixirumSounds.BLOCK_CAULDRON_BOIL, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.x = entity.getBlockPos().getX();
        this.y = entity.getBlockPos().getY();
        this.z = entity.getBlockPos().getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0f;
    }

    public boolean isFor(GlassCauldronEntity entity) {
        return this.entity == entity;
    }

    @Override
    public void tick() {
        if (entity.isRemoved() || entity.temperature <= 0.0) stop();
        this.volume = (float) Math.pow(entity.temperature, 5.0);
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public static void maybePlayFor(GlassCauldronEntity entity) {
        for (var trackedSound : TRACKED_SOUNDS) {
            if (trackedSound.isFor(entity)) return;
        }
        var sound = new CauldronSoundInstance(entity);
        Minecraft.getInstance().getSoundManager().play(sound);
        TRACKED_SOUNDS.add(sound);
    }

    public static void tickTracked() {
        TRACKED_SOUNDS.removeIf(AbstractTickableSoundInstance::isStopped);
    }
}