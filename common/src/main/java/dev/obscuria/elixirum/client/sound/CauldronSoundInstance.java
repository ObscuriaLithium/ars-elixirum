package dev.obscuria.elixirum.client.sound;

import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public final class CauldronSoundInstance extends AbstractTickableSoundInstance
{
    private static final List<CauldronSoundInstance> TRACKED_SOUNDS = Lists.newArrayList();
    private final GlassCauldronEntity entity;

    public CauldronSoundInstance(GlassCauldronEntity entity)
    {
        super(ElixirumSounds.BLOCK_CAULDRON_BOIL, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.x = entity.getBlockPos().getX();
        this.y = entity.getBlockPos().getY();
        this.z = entity.getBlockPos().getZ();
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = 0f;
    }

    public boolean is(GlassCauldronEntity entity)
    {
        return this.entity == entity;
    }

    @Override
    public void tick()
    {
        if (entity.isRemoved() || entity.getTemperature() <= 0.0) this.stop();
        this.volume = (float) Math.pow(entity.getTemperature(), 5);
    }

    @Override
    public boolean canStartSilent()
    {
        return true;
    }

    @ApiStatus.Internal
    public static void play(GlassCauldronEntity entity)
    {
        if (TRACKED_SOUNDS.stream().anyMatch(sound -> sound.is(entity))) return;
        final var sound = new CauldronSoundInstance(entity);
        Minecraft.getInstance().getSoundManager().play(sound);
        TRACKED_SOUNDS.add(sound);
    }

    @ApiStatus.Internal
    public static void onClientTick()
    {
        TRACKED_SOUNDS.removeIf(AbstractTickableSoundInstance::isStopped);
    }
}
