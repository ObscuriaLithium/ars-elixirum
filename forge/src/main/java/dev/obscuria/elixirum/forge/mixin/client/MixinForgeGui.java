package dev.obscuria.elixirum.forge.mixin.client;

import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeGui.class, remap = false)
public abstract class MixinForgeGui {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hideGuiOnScreen(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof AlchemyScreen) ci.cancel();
    }
}
