package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.common.alchemy.codex.components.KnownRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ScoreDetails extends AbstractDetails {

    private final float scale;
    private boolean backgroundFlag = true;

    public ScoreDetails(CachedElixir elixir) {
        super(ElixirumUI.DETAILS_SCORE);
        this.scale = GuiToolkit.snapScale(GuiToolkit.PARAGRAPH_SCALE);
        var entry = findEntry(elixir);
        this.addChild(new Entry(ElixirumUI.SCORE_BEST_QUALITY,
                entry.map(this::bestQuality).orElseGet(this::zero)));
        this.addChild(new Entry(ElixirumUI.SCORE_TIMES_BREWED,
                entry.map(this::timesBrewed).orElseGet(this::zero)));
        this.addChild(new Entry(ElixirumUI.SCORE_FABLED_INSTANCES,
                entry.map(this::fabledInstances).orElseGet(this::zero)));
    }

    private Component bestQuality(KnownRecipes.Entry entry) {
        ;
        return Component.literal(String.valueOf(entry.getBestQuality()));
    }

    private Component timesBrewed(KnownRecipes.Entry entry) {
        return Component.literal(String.valueOf(entry.getBrewCount()));
    }

    private Component fabledInstances(KnownRecipes.Entry entry) {
        return Component.literal(String.valueOf(entry.getFabledInstances()));
    }

    private Optional<KnownRecipes.Entry> findEntry(CachedElixir elixir) {
        var component = ClientAlchemy.localProfile().knownRecipes();
        return Optional.ofNullable(component.entries.get(elixir.recipe().uuid()));
    }

    private Component zero() {
        return Component.literal("0");
    }

    private class Entry extends Control {

        private final Component name;
        private final Component value;
        private final boolean drawBackground;

        public Entry(Component name, Component value) {
            super(0, 0, 0, 14, CommonComponents.EMPTY);
            this.name = name;
            this.value = value;
            this.drawBackground = ScoreDetails.this.backgroundFlag;
            ScoreDetails.this.backgroundFlag = !ScoreDetails.this.backgroundFlag;
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {

            var font = Minecraft.getInstance().font;
            if (drawBackground) {
                context.pushModulate(Palette.DARKEST);
                GuiToolkit.draw(graphics, Textures.SOLID_WHITE, this);
                context.popModulate();
            }

            graphics.pose().pushPose();
            graphics.pose().translate(getX() + 4, getCenterY(), 0f);

            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1f);
            graphics.pose().translate(0f, -5f * scale, 1f);
            graphics.drawString(font, name, 0, 0, Palette.LIGHT.decimal());
            graphics.pose().popPose();

            graphics.pose().translate(getWidth() - 8, 0f, 0f);

            graphics.pose().pushPose();
            graphics.pose().scale(scale, scale, 1f);
            graphics.pose().translate(-font.width(value), -5f * scale, 1f);
            graphics.drawString(font, value, 0, 0, Palette.MODERATE.decimal());
            graphics.pose().popPose();

            graphics.pose().popPose();
        }

        @Override
        protected void measure() {
            setMeasuredSize(0, (int) (6f + 10f * scale));
        }

        @Override
        protected boolean hasOwnContent() {
            return true;
        }
    }
}
