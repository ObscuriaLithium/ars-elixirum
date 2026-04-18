package dev.obscuria.elixirum.common.world.block.entity;

import com.google.common.collect.ImmutableList;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.common.CodecHelper;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import dev.obscuria.elixirum.common.registry.ElixirumBlockEntities;
import dev.obscuria.elixirum.common.registry.ElixirumItems;
import dev.obscuria.elixirum.common.world.block.GlassCauldronBehavior;
import dev.obscuria.elixirum.common.world.block.GlassCauldronInteraction;
import dev.obscuria.elixirum.common.world.particle.BubbleParticleOptions;
import dev.obscuria.elixirum.common.world.particle.SplashParticleOptions;
import dev.obscuria.elixirum.server.alchemy.brewing.IngredientMixer;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Function;

public class GlassCauldronEntity extends BlockEntity {

    private static final String TAG_IS_FILLED = "IsFilled";
    private static final String TAG_TEMPERATURE = "Temperature";
    private static final String TAG_AVAILABLE_PORTIONS = "AvailablePortions";
    private static final String TAG_MIXER = "Mixer";

    public boolean isFilled;
    public double temperature;
    public int availablePortions;

    private IngredientMixer mixer;
    private float yRot;
    private float yRotO;

    public GlassCauldronEntity(BlockPos pos, BlockState blockState) {
        super(ElixirumBlockEntities.GLASS_CAULDRON.get(), pos, blockState);
        this.mixer = new IngredientMixer();
    }

    public AlchemyRecipe recipe() {
        return mixer.recipe();
    }

    public boolean isEmpty() {
        return !isFilled;
    }

    public boolean isBoiling() {
        return temperature >= 1.0;
    }

    public ContentType contentType() {
        if (isEmpty()) return ContentType.NONE;
        if (!mixer.isEmpty()) return ContentType.ELIXIR;
        return ContentType.WATER;
    }

    public boolean maybeFillWater() {
        if (isFilled) return false;
        this.isFilled = true;
        this.temperature = 0.0;
        this.availablePortions = 3;
        this.setChanged();
        this.updateClients();
        return true;
    }

    public boolean maybeFlush() {
        if (!isFilled) return false;
        this.isFilled = false;
        this.temperature = 0.0;
        this.availablePortions = 0;
        this.mixer.clear();
        this.setChanged();
        this.updateClients();
        return true;
    }

    public boolean appendIngredient(ItemStack stack) {
        if (level == null) return false;
        if (temperature < 1.0) return false;
        if (!mixer.append(this, stack)) return false;
        this.setChanged();
        this.updateClients();
        this.playSound(SoundEvents.AXOLOTL_SPLASH, 1f, 1f);
        this.sendSplashParticles(32);
        return true;
    }

    public ItemStack scoopUp() {
        if (!isFilled || !mixer.isComplete() || getLevel() == null) return ItemStack.EMPTY;
        final var stack = ElixirumItems.ELIXIR.instantiate();
        ArsElixirumHelper.setElixirContents(stack, mixer.getContents());
        this.availablePortions--;
        if (availablePortions <= 0) this.maybeFlush();
        return stack;
    }

    public boolean hasHeatSource() {
        if (level == null) return false;
        final var state = level.getBlockState(getBlockPos().below());
        if (!state.is(ArsElixirum.HEAT_SOURCE_BLOCKS)) return false;
        if (!state.hasProperty(BlockStateProperties.LIT)) return true;
        return state.getValue(BlockStateProperties.LIT);
    }

    public float computeYRot(float partialTicks) {
        return Mth.lerp(partialTicks, yRotO, yRot);
    }

    public void shiftTemperature(double amount) {
        final var temperature = Mth.clamp(this.temperature + amount, 0.0, 1.0);
        if (this.temperature == temperature) return;
        this.temperature = temperature;
        this.setChanged();
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (level == null) return;
        level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, volume, pitch);
    }

    public ImmutableList<GlassCauldronInteraction> actualInteractions() {
        return isFilled ? GlassCauldronInteraction.ELIXIR : GlassCauldronInteraction.EMPTY;
    }

    public void createSplashParticles(int count) {
        if (level == null) return;
        final var center = this.getBlockPos().getCenter();
        final var color = contentType().pickColor(this);
        for (var i = 0; i < count; i++) {
            level.addParticle(
                    new SplashParticleOptions(color, 1f),
                    center.x + level.random.triangle(0.0, 0.25),
                    center.y + 0.4f,
                    center.z + level.random.triangle(0.0, 0.25),
                    0.0, 1.0, 0.0);
        }
    }

    public void createBubbleParticles(int count) {
        if (level == null) return;
        final var center = this.getBlockPos().getCenter();
        final var color = contentType().pickColor(this);
        for (var i = 0; i < count; i++) {
            level.addParticle(
                    new BubbleParticleOptions(color, 1f),
                    center.x + level.random.triangle(0.0, 0.35),
                    center.y + 0.3f,
                    center.z + level.random.triangle(0.0, 0.35),
                    0.0, 0.0, 0.0);
        }
    }

    public void sendSplashParticles(int count) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        final var center = this.getBlockPos().getCenter();
        final var color = contentType().pickColor(this);
        serverLevel.sendParticles(new SplashParticleOptions(color, 1f),
                center.x, center.y + 0.4, center.z, count,
                0.15, 0.0, 0.15, 0.0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean(TAG_IS_FILLED, isFilled);
        tag.putDouble(TAG_TEMPERATURE, temperature);
        tag.putInt(TAG_AVAILABLE_PORTIONS, availablePortions);
        CodecHelper.save(tag, TAG_MIXER, IngredientMixer.CODEC, mixer);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.isFilled = tag.getBoolean(TAG_IS_FILLED);
        this.temperature = tag.getDouble(TAG_TEMPERATURE);
        this.availablePortions = tag.getInt(TAG_AVAILABLE_PORTIONS);
        this.mixer = CodecHelper.load(tag, TAG_MIXER, IngredientMixer.CODEC, mixer);
    }

    private void updateClients() {
        if (level == null) return;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GlassCauldronEntity entity) {
        for (var behavior : GlassCauldronBehavior.QUEUE) {
            if (!behavior.shouldTick(entity)) continue;
            if (behavior.tick(entity, level, pos, state)) break;
        }
        if (!level.isClientSide()) return;
        entity.yRotO = entity.yRot;
        entity.yRot += 0.01f + (float) entity.temperature * 0.1f;
    }

    public enum ContentType {
        NONE(false, ContentType::defaultColor),
        WATER(false, ContentType::waterColor),
        ELIXIR(true, ContentType::elixirColor),
        WUNSCHPUNSCH(true, ContentType::wunschpunschColor);

        public static final RGB COLOR_WATER = Colors.rgbOf(0x5575DD);
        public static final RGB COLOR_BOILING_WATER = Colors.rgbOf(0xAABBDD);
        public static final RGB COLOR_WUNSCHPUNSCH = Colors.rgbOf(0x7f0080);

        @Getter private final boolean isGlowing;
        private final Function<GlassCauldronEntity, RGB> colorProvider;

        ContentType(boolean isGlowing, Function<GlassCauldronEntity, RGB> colorProvider) {
            this.colorProvider = colorProvider;
            this.isGlowing = isGlowing;
        }

        public boolean isNone() {
            return this == NONE;
        }

        public RGB pickColor(GlassCauldronEntity entity) {
            return colorProvider.apply(entity);
        }

        private static RGB defaultColor(GlassCauldronEntity entity) {
            return COLOR_WATER;
        }

        private static RGB waterColor(GlassCauldronEntity entity) {
            return COLOR_WATER.lerp(COLOR_BOILING_WATER, (float) entity.temperature);
        }

        private static RGB elixirColor(GlassCauldronEntity entity) {
            return entity.getLevel() != null
                    ? entity.mixer.getContents().color()
                    : ElixirContents.WATER.color();
        }

        private static RGB wunschpunschColor(GlassCauldronEntity entity) {
            return COLOR_WUNSCHPUNSCH;
        }
    }
}
