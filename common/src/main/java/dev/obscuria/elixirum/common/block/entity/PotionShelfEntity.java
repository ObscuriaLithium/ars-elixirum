package dev.obscuria.elixirum.common.block.entity;

import dev.obscuria.elixirum.common.ElixirumTags;
import dev.obscuria.elixirum.registry.ElixirumBlockEntityTypes;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public final class PotionShelfEntity extends BlockEntity
{
    private static final String TAG_ITEM_1 = "Item1";
    private static final String TAG_ITEM_2 = "Item2";
    private static final String TAG_ITEM_3 = "Item3";
    private ItemStack stack1 = ItemStack.EMPTY;
    private ItemStack stack2 = ItemStack.EMPTY;
    private ItemStack stack3 = ItemStack.EMPTY;

    public PotionShelfEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public PotionShelfEntity(BlockPos pos, BlockState state)
    {
        this(ElixirumBlockEntityTypes.POTION_SHELF.value(), pos, state);
    }

    public ItemStack getFirstStack()
    {
        return this.stack1;
    }

    public void setFirstStack(ItemStack stack)
    {
        this.stack1 = stack;
        this.setChanged();
        this.updateClients();
    }

    public ItemStack getSecondStack()
    {
        return this.stack2;
    }

    public void setSecondStack(ItemStack stack)
    {
        this.stack2 = stack;
        this.setChanged();
        this.updateClients();
    }

    public ItemStack getThirdStack()
    {
        return this.stack3;
    }

    public void setThirdStack(ItemStack stack)
    {
        this.stack3 = stack;
        this.setChanged();
        this.updateClients();
    }

    public boolean putFirstStack(ItemStack stack)
    {
        if (!this.getFirstStack().isEmpty()) return false;
        if (!this.validateStack(stack)) return false;
        this.setFirstStack(stack.copyWithCount(1));
        this.playSound(ElixirumSounds.ITEM_BOTTLE_PUT, 1f);
        this.playSound(ElixirumSounds.ITEM_BOTTLE_SHAKE, 0.5f);
        stack.shrink(1);
        return true;
    }

    public boolean putSecondStack(ItemStack stack)
    {
        if (!this.getSecondStack().isEmpty()) return false;
        if (!this.validateStack(stack)) return false;
        this.setSecondStack(stack.copyWithCount(1));
        this.playSound(ElixirumSounds.ITEM_BOTTLE_PUT, 1f);
        this.playSound(ElixirumSounds.ITEM_BOTTLE_SHAKE, 0.5f);
        stack.shrink(1);
        return true;
    }

    public boolean putThirdStack(ItemStack stack)
    {
        if (!this.getThirdStack().isEmpty()) return false;
        if (!this.validateStack(stack)) return false;
        this.setThirdStack(stack.copyWithCount(1));
        this.playSound(ElixirumSounds.ITEM_BOTTLE_PUT, 1f);
        this.playSound(ElixirumSounds.ITEM_BOTTLE_SHAKE, 0.5f);
        stack.shrink(1);
        return true;
    }

    public ItemStack takeFirstStack()
    {
        if (this.getFirstStack().isEmpty()) return ItemStack.EMPTY;
        final var stack = this.getFirstStack();
        this.setFirstStack(ItemStack.EMPTY);
        this.playSound(ElixirumSounds.ITEM_BOTTLE_SLOSH, 0.5f);
        return stack;
    }

    public ItemStack takeSecondStack()
    {
        if (this.getSecondStack().isEmpty()) return ItemStack.EMPTY;
        final var stack = this.getSecondStack();
        this.setSecondStack(ItemStack.EMPTY);
        this.playSound(ElixirumSounds.ITEM_BOTTLE_SLOSH, 0.5f);
        return stack;
    }

    public ItemStack takeThirdStack()
    {
        if (this.getThirdStack().isEmpty()) return ItemStack.EMPTY;
        final var stack = this.getThirdStack();
        this.setThirdStack(ItemStack.EMPTY);
        this.playSound(ElixirumSounds.ITEM_BOTTLE_SLOSH, 0.5f);
        return stack;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        return this.saveCustomOnly(provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.loadAdditional(tag, provider);
        if (tag.contains(TAG_ITEM_1, Tag.TAG_COMPOUND))
            this.stack1 = ItemStack.parseOptional(provider, tag.getCompound(TAG_ITEM_1));
        if (tag.contains(TAG_ITEM_2, Tag.TAG_COMPOUND))
            this.stack2 = ItemStack.parseOptional(provider, tag.getCompound(TAG_ITEM_2));
        if (tag.contains(TAG_ITEM_3, Tag.TAG_COMPOUND))
            this.stack3 = ItemStack.parseOptional(provider, tag.getCompound(TAG_ITEM_3));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        super.saveAdditional(tag, provider);
        tag.put(TAG_ITEM_1, this.stack1.saveOptional(provider));
        tag.put(TAG_ITEM_2, this.stack2.saveOptional(provider));
        tag.put(TAG_ITEM_3, this.stack3.saveOptional(provider));
    }

    @SuppressWarnings("all")
    private boolean validateStack(ItemStack stack)
    {
        return stack.is(ElixirumTags.Items.POTION_SHELF_PLACEABLE);
    }

    private void playSound(SoundEvent event, float volume)
    {
        if (this.level == null) return;
        this.level.playSound(null, getBlockPos(), event, SoundSource.BLOCKS, volume, 1f);
    }

    private void updateClients()
    {
        if (level != null) this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(),
                Block.UPDATE_CLIENTS);
    }
}
