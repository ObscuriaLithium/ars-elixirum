package dev.obscuria.elixirum.client.screen.toolkit.tools;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@AllArgsConstructor
public abstract class Texture {

    protected final ResourceLocation location;
    protected final int width;
    protected final  int height;

    public static Texture fixed(ResourceLocation location, int width, int height) {
        return new FixedTexture(location, width, height, 0, 0, width, height);
    }

    public static Texture fixed(ResourceLocation location, int width, int height, int u, int v, int textureWidth, int textureHeight) {
        return new FixedTexture(location, width, height, u, v, textureWidth, textureHeight);
    }

    public static Texture nineSliced(ResourceLocation location, int width, int height, int borders, int u, int v) {
        return new NineSlicedTexture(location, width, height, borders, u, v);
    }

    public static Texture stretched(ResourceLocation location) {
        return new StretchedTexture(location);
    }

    public abstract void render(GuiGraphics graphics, int x, int y, int width, int height);

    private static class FixedTexture extends Texture {

        private final int u;;
        private final int v;
        private final int textureWidth;
        private final int textureHeight;

        public FixedTexture(ResourceLocation location, int width, int height, int u, int v, int textureWidth, int textureHeight) {
            super(location, width, height);
            this.u = u;
            this.v = v;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y, int width, int height) {
            graphics.blit(location, x, y, u, v, width, height, width, height);
        }
    }

    private static class NineSlicedTexture extends Texture {

        private final int borders;
        private final int u;
        private final int v;

        public NineSlicedTexture(ResourceLocation location, int width, int height, int borders, int u, int v) {
            super(location, width, height);
            this.borders = borders;
            this.u = u;
            this.v = v;
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y, int width, int height) {
            if (width == 0 || height == 0) return;
            graphics.blitNineSliced(location, x, y, width, height, borders, this.width, this.height, u, v);
        }
    }

    private static class StretchedTexture extends Texture {

        public StretchedTexture(ResourceLocation location) {
            super(location, 0, 0);
        }

        @Override
        public void render(GuiGraphics graphics, int x, int y, int width, int height) {
            graphics.blit(location, x, y, 0, 0, width, height, width, height);
        }
    }
}
