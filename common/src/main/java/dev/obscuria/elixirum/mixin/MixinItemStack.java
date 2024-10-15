package dev.obscuria.elixirum.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.obscuria.elixirum.common.hooks.ItemStackHooks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    @Inject(method = "getTooltipLines",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/core/component/DataComponents;UNBREAKABLE:Lnet/minecraft/core/component/DataComponentType;",
                    shift = At.Shift.AFTER,
                    ordinal = 0))
    private void getTooltipLines_Modify(Item.TooltipContext context,
                                        @Nullable Player player,
                                        TooltipFlag flag,
                                        CallbackInfoReturnable<List<Component>> info,
                                        @Local Consumer<Component> consumer)
    {
        ItemStackHooks.getTooltipLines((ItemStack) (Object) this, player, consumer);
    }
}
