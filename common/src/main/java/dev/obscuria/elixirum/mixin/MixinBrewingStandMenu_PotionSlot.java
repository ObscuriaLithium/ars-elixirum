package dev.obscuria.elixirum.mixin;

import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.elixirum.common.network.ClientboundDiscoverEssencePayload;
import dev.obscuria.elixirum.helpers.BrewingHelper;
import dev.obscuria.elixirum.server.alchemy.ServerAlchemy;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public abstract class MixinBrewingStandMenu_PotionSlot {

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void mayPlaceCustomInput(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!BrewingHelper.canPlaceInPotionSlot(stack)) return;
        cir.setReturnValue(true);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void takeExtract(Player player, ItemStack stack, CallbackInfo ci) {
        var contents = ArsElixirumAPI.getExtractContents(stack);
        if (contents.isEmpty()) return;
        if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getServer() == null) return;
            var profile = ServerAlchemy.get(serverPlayer.getServer()).profileOf(serverPlayer);
            for (var essenceHolder : contents.essences().sorted()) {
                var item = contents.source();
                var essence = essenceHolder.getKey();
                if (!DiscoverySystem.discoverEssence(profile, item, essence)) return;
                FragmentumNetworking.sendTo(serverPlayer, new ClientboundDiscoverEssencePayload(item, essence));
            }
        }
    }
}
