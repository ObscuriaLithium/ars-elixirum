package dev.obscuria.elixirum.common.block.entity;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.ElixirumClient;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.common.ElixirumTags;
import dev.obscuria.elixirum.common.alchemy.brewing.IngredientMixer;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.particle.ElixirBubbleParticleOptions;
import dev.obscuria.elixirum.common.particle.ElixirSplashParticleOptions;
import dev.obscuria.elixirum.registry.ElixirumBlockEntityTypes;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import dev.obscuria.elixirum.registry.ElixirumItems;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.ServerAlchemy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.function.Function;
import java.util.function.Predicate;

public final class GlassCauldronEntity extends BlockEntity {
    private static final String TAG_FILLED = "Filled";
    private static final String TAG_TEMPERATURE = "Temperature";
    private static final String TAG_BOTTLES = "Bottles";
    private static final String TAG_ELIXIR_MIXER = "ElixirMixer";
    private IngredientMixer mixer = new IngredientMixer();
    private boolean filled = false;
    private double temperature = 0f;
    private int bottles;
    private float rotation;
    private float rotationO;

    public GlassCauldronEntity(BlockPos pos, BlockState state) {
        super(ElixirumBlockEntityTypes.GLASS_CAULDRON, pos, state);
    }

    public boolean isEmpty() {
        return !this.filled;
    }

    public boolean isFilled() {
        return this.filled;
    }

    public boolean isBoil() {
        return this.temperature >= 1;
    }

    public boolean hasElixir() {
        return this.isFilled() && !this.mixer.isEmpty();
    }

    public double getTemperature() {
        return this.temperature;
    }

    public void fillWithWater() {
        this.filled = true;
        this.temperature = 0;
        this.bottles = 3;
        this.setChanged();
        this.updateClients();
    }

    public void pickUpWater() {
        this.filled = false;
        this.temperature = 0;
        this.bottles = 0;
        this.setChanged();
        this.updateClients();
    }

    public void flushElixir() {
        this.filled = false;
        this.temperature = 0;
        this.bottles = 0;
        this.mixer.clear();
        this.setChanged();
        this.updateClients();
    }

    public ItemStack brew(Player player) {
        final var contents = this.mixer.getResult(essenceGetter());
        if (contents.isEmpty()) return ItemStack.EMPTY;
        final var stack = ElixirumItems.ELIXIR.value().getDefaultInstance();
        stack.set(ElixirumDataComponents.ELIXIR_CONTENTS, contents);
        if (player instanceof ServerPlayer serverPlayer) {
            ServerAlchemy.getProfile(serverPlayer)
                    .searchInCollection(mixer.getRecipe())
                    .ifPresent(holder -> holder.applyAppearance(stack));
        } else {
            ClientAlchemy.getCache().saveRecent(mixer.getRecipe());
        }

        this.bottles -= 1;
        if (bottles <= 0)
            this.flushElixir();
        return stack;
    }

    public float getRotation(float delta) {
        return Mth.lerp(delta, this.rotationO, this.rotation);
    }

    public void rotate(float radians) {
        this.rotationO = this.rotation;
        this.rotation += radians;
    }

    public ContentType getContentType() {
        if (this.isEmpty()) return ContentType.NONE;
        if (this.hasElixir()) return ContentType.ELIXIR;
        return ContentType.WATER;
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (this.level != null)
            this.level.playSound(null, this.getBlockPos(), sound,
                    SoundSource.BLOCKS, volume, pitch);
    }

    public boolean hasHeatSource() {
        if (this.level == null) return false;
        final var state = this.level.getBlockState(this.getBlockPos().below());
        if (state.is(ElixirumTags.Blocks.HEAT_SOURCES))
            return state.hasProperty(BlockStateProperties.LIT)
                    ? state.getValue(BlockStateProperties.LIT)
                    : true;
        return false;
    }

    public void addTemperature(double amount) {
        final var temperature = Math.clamp(this.temperature + amount, 0, 1);
        if (this.temperature != temperature) {
            this.temperature = temperature;
            this.setChanged();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GlassCauldronEntity entity) {
        for (var behavior : BehaviorQueue.values())
            if (behavior.canUse(entity))
                if (behavior.tick(level, pos, state, entity))
                    break;
        if (!level.isClientSide) return;
        entity.rotate(0.01f + (float) entity.temperature * 0.1f);
    }

    public boolean addIngredient(ItemStack stack) {
        if (level == null) return false;
        if (mixer.append(stack.getItem())) {
            if (level.isClientSide) {
                this.createSplashParticles(20);
            } else {
                stack.shrink(1);
                this.setChanged();
                this.updateClients();
                this.playSound(SoundEvents.AXOLOTL_SPLASH, 1, 1);
            }
            return true;
        }
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(TAG_FILLED))
            this.filled = tag.getBoolean(TAG_FILLED);
        if (tag.contains(TAG_TEMPERATURE))
            this.temperature = tag.getDouble(TAG_TEMPERATURE);
        if (tag.contains(TAG_BOTTLES))
            this.bottles = tag.getInt(TAG_BOTTLES);
        if (tag.contains(TAG_ELIXIR_MIXER, Tag.TAG_COMPOUND)) {
            final var registryOps = RegistryOps.create(NbtOps.INSTANCE, provider);
            IngredientMixer.CODEC.decode(registryOps, tag.getCompound(TAG_ELIXIR_MIXER))
                    .ifSuccess(result -> this.mixer = result.getFirst())
                    .ifError(error -> {
                        Elixirum.LOG.warn("Failed to decode ElixirMixer for cauldron");
                        Elixirum.LOG.warn(error.message());
                    });
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean(TAG_FILLED, this.filled);
        tag.putDouble(TAG_TEMPERATURE, this.temperature);
        tag.putInt(TAG_BOTTLES, this.bottles);
        final var registryOps = RegistryOps.create(NbtOps.INSTANCE, provider);
        IngredientMixer.CODEC.encodeStart(registryOps, this.mixer)
                .ifSuccess(result -> tag.put(TAG_ELIXIR_MIXER, result))
                .ifError(error -> {
                    Elixirum.LOG.warn("Failed to encode ElixirMixer for cauldron");
                    Elixirum.LOG.warn(error.message());
                });
    }

    private void consumeNearbyIngredients() {
        if (level == null) return;
        for (var entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()))) {
            final var stack = entity.getItem();
            if (this.addIngredient(stack))
                entity.setItem(stack);
        }
    }

    private void createSplashParticles(int count) {
        if (this.level == null) return;
        final var center = this.getBlockPos().getCenter();
        final var color = this.getContentType().getColor(this);
        for (int i = 0; i < count; i++)
            this.level.addParticle(ElixirSplashParticleOptions.parse(color),
                    center.x + this.level.random.triangle(0, 0.25f),
                    center.y + 0.4f,
                    center.z + this.level.random.triangle(0, 0.25f),
                    0, 1, 0);
    }

    private void createBubbleParticles(int count) {
        if (this.level == null) return;
        final var center = this.getBlockPos().getCenter();
        final var color = this.getContentType().getColor(this);
        for (int i = 0; i < count; i++)
            this.level.addParticle(ElixirBubbleParticleOptions.parse(color),
                    center.x + this.level.random.triangle(0, 0.35f),
                    center.y + 0.3f,
                    center.z + this.level.random.triangle(0, 0.35f),
                    0, 0, 0);
    }

    private void updateClients() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS);
    }

    private HolderGetter<Essence> essenceGetter() {
        if (level == null) throw new IllegalStateException("Level should not be null");
        return level.holderLookup(ElixirumRegistries.ESSENCE);
    }

    public enum ContentType {
        NONE(false, entity -> 0xFFFFFFFF),
        WATER(false, entity -> FastColor.ARGB32.lerp((float) entity.getTemperature(), 0xFF5575DD, 0xFFAABBDD)),
        ELIXIR(true, entity -> entity.mixer.getColor(entity.essenceGetter())),
        WUNSCHPUNSCH(true, entity -> 0xFF80FF80);

        private final boolean glowing;
        private final Function<GlassCauldronEntity, Integer> colorFunction;

        ContentType(boolean glowing, Function<GlassCauldronEntity, Integer> colorFunction) {
            this.glowing = glowing;
            this.colorFunction = colorFunction;
        }

        public int getColor(GlassCauldronEntity entity) {
            return this.colorFunction.apply(entity);
        }

        public boolean isGlowing() {
            return this.glowing;
        }

        public boolean isNone() {
            return this == NONE;
        }
    }

    private enum BehaviorQueue {
        COOLING_DOWN(entity -> entity.isFilled()
                && !entity.hasHeatSource()
                && entity.temperature > 0.0, BehaviorQueue::tickCoolingDown),
        HEATING(entity -> entity.isFilled()
                && entity.hasHeatSource()
                && entity.getTemperature() < 1.0, BehaviorQueue::tickHeating),
        BOILING(entity -> entity.isFilled()
                && entity.hasHeatSource()
                && entity.getTemperature() >= 1.0, BehaviorQueue::tickBoiling);

        private final Predicate<GlassCauldronEntity> predicate;
        private final Behavior behavior;

        BehaviorQueue(Predicate<GlassCauldronEntity> predicate, Behavior behavior) {
            this.predicate = predicate;
            this.behavior = behavior;
        }

        public boolean canUse(GlassCauldronEntity entity) {
            return this.predicate.test(entity);
        }

        public boolean tick(Level level, BlockPos pos, BlockState state, GlassCauldronEntity entity) {
            return this.behavior.tick(level, pos, state, entity);
        }

        private static boolean tickCoolingDown(Level level, BlockPos pos, BlockState state, GlassCauldronEntity entity) {
            entity.addTemperature(-0.002);
            if (level.isClientSide) {
                final var random = level.random.nextFloat();
                if (random <= 0.2f * entity.getTemperature())
                    entity.createSplashParticles(1);
            }
            return true;
        }

        private static boolean tickHeating(Level level, BlockPos pos, BlockState state, GlassCauldronEntity entity) {
            entity.addTemperature(0.002);
            if (level.isClientSide) {
                ElixirumClient.playBoilingSound(entity);
                final var random = level.random.nextFloat();
                if (random <= 0.2f * entity.getTemperature())
                    entity.createSplashParticles(1);
            }
            return true;
        }

        private static boolean tickBoiling(Level level, BlockPos pos, BlockState state, GlassCauldronEntity entity) {
            entity.consumeNearbyIngredients();
            if (level.isClientSide) {
                ElixirumClient.playBoilingSound(entity);
                entity.createSplashParticles(1);
                entity.createBubbleParticles(1);
            }
            return true;
        }

        @FunctionalInterface
        private interface Behavior {

            boolean tick(Level level, BlockPos pos, BlockState state, GlassCauldronEntity entity);
        }
    }
}
