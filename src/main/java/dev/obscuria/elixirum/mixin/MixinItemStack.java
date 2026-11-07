package dev.obscuria.elixirum.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import dev.obscuria.elixirum.common.hooks._ItemStackHooks;
import dev.obscuria.elixirum.common.world.ItemStackCache;
import dev.obscuria.fragmentum.world.tooltip.GroupTooltip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(value = ItemStack.class)
public abstract class MixinItemStack implements ItemStackCache.Provider {

    //? if <1.21 {
    @Shadow @Nullable private CompoundTag tag;
    //? }
    @Unique @Nullable private Object elixirum$cachedTag;
    @Unique @Nullable private ItemStackCache elixirum$cache;
    @Unique @Nullable private List<EssenceHolder> elixirum$suppressedEssences;
    @Unique private boolean elixirum$isDirty;
    @Unique private boolean elixirum$brewFlag;

    @Override
    public ItemStackCache elixirum$getCache() {
        //? if >=1.21 {
        /*var tag = ((ItemStack) (Object) this).get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        *///? }
        if (elixirum$isDirty || !Objects.equals(elixirum$cachedTag, tag)) {
            this.elixirum$cache = ItemStackCache.build((ItemStack) (Object) this);
            this.elixirum$cachedTag = tag;
            this.elixirum$isDirty = false;
        }
        return elixirum$cache == null ? ItemStackCache.EMPTY : elixirum$cache;
    }

    @Override
    public void elixirum$markDirty() {
        this.elixirum$isDirty = true;
    }

    @Override
    public List<EssenceHolder> elixirum$suppressedEssences() {
        if (elixirum$suppressedEssences != null) return elixirum$suppressedEssences;
        this.elixirum$suppressedEssences = new ArrayList<>();
        return elixirum$suppressedEssences;
    }

    @Override
    public void elixirum$setBrewFlag(boolean flag) {
        this.elixirum$brewFlag = flag;
    }

    @Override
    public boolean elixirum$isBrewFlag() {
        return elixirum$brewFlag;
    }
    //? if >=1.21 {
    /*@ModifyReturnValue(method = "getRarity", at = @At("RETURN"))
    private net.minecraft.world.item.Rarity elixirum$rarity(net.minecraft.world.item.Rarity original) {
        var stack = (ItemStack) (Object) this;
        return stack.getItem() instanceof dev.obscuria.elixirum.common.world.item.ElixirItem
                || stack.getItem() instanceof dev.obscuria.elixirum.common.world.item.WitchTotemOfUndyingItem
                ? dev.obscuria.elixirum.common.alchemy.ElixirQuality.fromStack(stack).getRarity() : original;
    }
    */
    //? }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyReturnValue(method = "getTooltipImage", at = @At("RETURN"))
    private Optional<TooltipComponent> injectTooltipImages(Optional<TooltipComponent> original) {
        final var self = (ItemStack) (Object) this;
        final @Nullable var image = _ItemStackHooks.injectTooltipImage(self);
        if (image == null) return original;
        return Optional.of(GroupTooltip.maybeGroup(original.orElse(null), image));
    }

}
