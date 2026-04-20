package dev.obscuria.elixirum.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.obscuria.elixirum.common.Hooks;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
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

    @Shadow @Nullable private CompoundTag tag;
    @Unique @Nullable private Tag elixirum$cachedTag;
    @Unique @Nullable private ItemStackCache elixirum$cache;
    @Unique @Nullable private List<EssenceHolder> elixirum$suppressedEssences;
    @Unique private boolean elixirum$isDirty;
    @Unique private boolean elixirum$brewFlag;

    @Override
    public ItemStackCache elixirum$getCache() {
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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyReturnValue(method = "getTooltipImage", at = @At("RETURN"))
    private Optional<TooltipComponent> injectTooltipImages(Optional<TooltipComponent> original) {
        final var self = (ItemStack) (Object) this;
        final @Nullable var image = Hooks.injectTooltipImage(self);
        if (image == null) return original;
        return Optional.of(GroupTooltip.maybeGroup(original.orElse(null), image));
    }
}
