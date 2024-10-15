package dev.obscuria.elixirum.mixin.client;

import dev.obscuria.elixirum.client.screen.FunctionalityTip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandScreen.class)
public abstract class MixinBrewingStandScreen extends AbstractContainerScreen<BrewingStandMenu>
{
    private MixinBrewingStandScreen(BrewingStandMenu menu,
                                    Inventory inventory,
                                    Component title)
    {
        super(menu, inventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init_Modify(CallbackInfo info)
    {
        this.addRenderableWidget(new FunctionalityTip(
                (width + imageWidth) / 2 + 2,
                (height - imageHeight) / 2 + 2));
    }
}
