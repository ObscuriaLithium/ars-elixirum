package dev.obscuria.elixirum.client.screen.tooltip.components;

import dev.obscuria.elixirum.common.alchemy.basics.EffectProvider;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class EffectListComponent implements ClientTooltipComponent {

    private final List<? extends Entry> entries;

    public EffectListComponent(ElixirContents contents) {
        this.entries = builtEntries(contents);
    }

    @Override
    public int getHeight() {
        return Entry.HEIGHT * entries.size();
    }

    @Override
    public int getWidth(Font font) {
        var maxEntryWidth = 0;
        for (var entry : entries)
            maxEntryWidth = Math.max(maxEntryWidth, entry.getWidth(font));
        return maxEntryWidth;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        var offset = 0;
        for (var entry : entries) {
            entry.render(font, x, y + offset, graphics);
            offset += Entry.HEIGHT;
        }
    }

    private List<Entry> builtEntries(ElixirContents contents) {
        var effects = new ArrayList<EffectProvider>();
        var sideEffects = new ArrayList<EffectProvider>();

        for (var provider : contents.effects()) {
            if (!provider.isVoided()) effects.add(provider);
            else sideEffects.add(provider);
        }

        var result = new ArrayList<Entry>();
        for (var provider : effects) {
            result.add(new Normal(provider));
        }

        if (!sideEffects.isEmpty() && contents.sideEffectProbability() > 0) {
            result.add(new SideEffects(contents, sideEffects));
        }

        return result;
    }

    private interface Entry {

        int HEIGHT = 20;

        int getWidth(Font font);

        void render(Font font, int x, int y, GuiGraphics graphics);
    }

    private static class Normal implements Entry {

        private final TextureAtlasSprite texture;
        private final Component firstLine;
        private final Component secondLine;

        public Normal(EffectProvider provider) {
            this.texture = Minecraft.getInstance().getMobEffectTextures().get(provider.mobEffect());
            this.firstLine = provider.displayNameWithPotency().copy().withStyle(ChatFormatting.GRAY);
            this.secondLine = provider.statusOrDuration().copy().withStyle(ChatFormatting.DARK_GRAY);
        }

        @Override
        public int getWidth(Font font) {
            return 20 + Math.max(font.width(firstLine), font.width(secondLine));
        }

        @Override
        public void render(Font font, int x, int y, GuiGraphics graphics) {
            graphics.blit(x, y, 0, 18, 18, texture);
            graphics.drawString(font, firstLine, x + 20, y, 0xFFFFFFFF);
            graphics.drawString(font, secondLine, x + 20, y + 10, 0xFFAAAAAA);
        }
    }

    private static class SideEffects implements Entry {

        private final List<EffectProvider> effects;
        private final Component firstLine;
        private final Component secondLine;

        public SideEffects(ElixirContents contents, List<EffectProvider> effects) {
            this.effects = effects;
            this.firstLine = (effects.size() == 1
                    ? Component.translatable("ui.elixirum.side_effects.one", effects.size())
                    : Component.translatable("ui.elixirum.side_effects.many", effects.size())
            ).withStyle(ChatFormatting.GRAY);
            this.secondLine = Component.translatable(
                    "ui.elixirum.side_effects.chance",
                    "%.0f%%".formatted(contents.sideEffectProbability() * 100f)
            ).withStyle(ChatFormatting.DARK_GRAY);
        }

        @Override
        public int getWidth(Font font) {
            return 20 + Math.max(font.width(firstLine), font.width(secondLine));
        }

        @Override
        public void render(Font font, int x, int y, GuiGraphics graphics) {
            var textures = Minecraft.getInstance().getMobEffectTextures();

            graphics.pose().pushPose();
            graphics.pose().translate(0, 9, 0);
            if (effects.size() >= 4) {
                graphics.blit(x + 9, y, 0, 9, 9, textures.get(effects.get(3).mobEffect()));
                graphics.blit(x, y, 0, 9, 9, textures.get(effects.get(2).mobEffect()));
                graphics.blit(x + 9, y - 9, 0, 9, 9, textures.get(effects.get(1).mobEffect()));
                graphics.blit(x, y - 9, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
            } else if (effects.size() >= 3) {
                graphics.blit(x + 9, y, 0, 9, 9, textures.get(effects.get(2).mobEffect()));
                graphics.blit(x, y, 0, 9, 9, textures.get(effects.get(1).mobEffect()));
                graphics.blit(x + 5, y - 9, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
            } else if (effects.size() >= 2) {
                graphics.blit(x + 9, y - 4, 0, 9, 9, textures.get(effects.get(1).mobEffect()));
                graphics.blit(x, y - 4, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
            } else if (!effects.isEmpty()) {
                graphics.blit(x + 5, y - 4, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
            }
            graphics.pose().popPose();

            graphics.drawString(font, firstLine, x + 20, y, 0xFFFFFFFF);
            graphics.drawString(font, secondLine, x + 20, y + 10, 0xFFAAAAAA);
        }
    }
}
