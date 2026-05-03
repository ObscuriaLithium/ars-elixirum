package dev.obscuria.elixirum.client.screen.alchemy.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class AspectFitImageControl extends Control {

    private final ResourceLocation texture;
    private final int textureWidth;
    private final int textureHeight;

    public AspectFitImageControl(ResourceLocation texture, int textureWidth, int textureHeight, int height) {
        super(0, 0, 0, height, CommonComponents.EMPTY);
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        if (!context.isVisible(this)) return;

        var scaleX = (float) getWidth() / textureWidth;
        var scaleY = (float) getHeight() / textureHeight;
        var scale = Math.min(scaleX, scaleY);

        var renderWidth = (int) (textureWidth * scale);
        var renderHeight = (int) (textureHeight * scale);

        var offsetX = getX() + (getWidth() - renderWidth) / 2;
        var offsetY = getY() + (getHeight() - renderHeight) / 2;

        RenderSystem.enableBlend();
        graphics.blit(
                texture,
                offsetX, offsetY,
                renderWidth, renderHeight,
                0, 0,
                textureWidth, textureHeight,
                textureWidth, textureHeight);
        RenderSystem.disableBlend();
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }
}
