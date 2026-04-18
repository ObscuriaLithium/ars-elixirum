package dev.obscuria.elixirum.common.world.block.entity;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.registry.ElixirumBlockEntities;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PotionShelfEntity extends BlockEntity {

    private static final String TAG_SLOT_1 = "Slot1";
    private static final String TAG_SLOT_2 = "Slot2";
    private static final String TAG_SLOT_3 = "Slot3";
    private final SimpleContainer container;

    public PotionShelfEntity(BlockPos pos, BlockState state) {
        super(ElixirumBlockEntities.POTION_SHELF.get(), pos, state);
        this.container = new SimpleContainer(3);
    }

    public ItemStack getItem(int slot) {
        return container.getItem(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        this.container.setItem(slot, stack);
        this.setChanged();
        updateClients();
    }

    public boolean placeItem(int slot, ItemStack stack) {
        if (!getItem(slot).isEmpty()) return false;
        if (!validate(stack)) return false;
        playSound(ElixirumSounds.ITEM_BOTTLE_PUT, 1f);
        playSound(ElixirumSounds.ITEM_BOTTLE_SHAKE, 0.5f);
        setItem(slot, stack.copyWithCount(1));
        stack.shrink(1);
        setChanged();
        updateClients();
        return true;
    }

    public ItemStack takeItem(int slot) {
        var stack = getItem(slot);
        if (stack.isEmpty()) return stack;
        playSound(ElixirumSounds.ITEM_BOTTLE_SLOSH, 0.5f);
        setItem(slot, ItemStack.EMPTY);
        setChanged();
        updateClients();
        return stack;
    }

    public List<ItemStack> removeAllItems() {
        var items = container.removeAllItems();
        setChanged();
        updateClients();
        return items;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(TAG_SLOT_1, getItem(0).save(new CompoundTag()));
        tag.put(TAG_SLOT_2, getItem(1).save(new CompoundTag()));
        tag.put(TAG_SLOT_3, getItem(2).save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_SLOT_1, Tag.TAG_COMPOUND))
            this.setItem(0, ItemStack.of(tag.getCompound(TAG_SLOT_1)));
        if (tag.contains(TAG_SLOT_2, Tag.TAG_COMPOUND))
            this.setItem(1, ItemStack.of(tag.getCompound(TAG_SLOT_2)));
        if (tag.contains(TAG_SLOT_3, Tag.TAG_COMPOUND))
            this.setItem(2, ItemStack.of(tag.getCompound(TAG_SLOT_3)));
    }

    private boolean validate(ItemStack stack) {
        return stack.is(ArsElixirum.POTION_SHELF_PLACEABLE_ITEMS);
    }

    private void playSound(SoundEvent event, float volume) {
        if (this.level == null) return;
        this.level.playSound(null, worldPosition, event, SoundSource.BLOCKS, volume, 1.0f);
    }

    private void updateClients() {
        if (this.level == null) return;
        var state = getBlockState();
        this.level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
    }
}
