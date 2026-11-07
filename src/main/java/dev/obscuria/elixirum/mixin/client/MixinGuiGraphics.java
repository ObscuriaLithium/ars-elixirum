package dev.obscuria.elixirum.mixin.client;

import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {

    @ModifyArg(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"), index = 4)
    private ClientTooltipPositioner modifyPositioner(ClientTooltipPositioner original) {
        if (!(Minecraft.getInstance().screen instanceof AlchemyScreen)) return original;
        return DefaultTooltipPositioner.INSTANCE;
    }
}
