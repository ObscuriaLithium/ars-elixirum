package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.FootnoteControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.layout.HSpacingControl;
import dev.obscuria.elixirum.common.alchemy.codex.components.AlchemyMastery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MasteryDetails extends AbstractDetails {

    public MasteryDetails(CachedElixir elixir) {
        super(ElixirumUI.DETAILS_MASTERY);
        this.addChild(new FootnoteControl(ElixirumUI.MASTERY_HINT));
        this.addChild(new HSpacingControl(6));
        this.addChild(new Progress(elixir.recipe()));
    }

    private static class Progress extends Control {

        private static final int[] MILESTONES = {25, 50, 75};

        private final int xp;
        private final float progress;
        private final float scale;

        public Progress(AlchemyRecipe recipe) {
            super(0, 0, 10, 4, CommonComponents.EMPTY);
            this.xp = ClientAlchemy.localProfile().mastery().getRecipeXp(recipe.uuid());
            this.progress = Mth.clamp(xp / (float) AlchemyMastery.MAX_RECIPE_XP, 0f, 1f);
            this.scale = GuiToolkit.snapScale(GuiToolkit.FOOTNOTE_SCALE);
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            int x = getX();
            int y = getY();
            int w = getWidth();
            int barY = y + 10;
            int barH = 4;

            renderXpRow(graphics, context, x, y, w);
            renderBar(graphics, context, x, barY, w, barH);
        }

        private void renderXpRow(GuiGraphics graphics, GuiContext context, int x, int y, int w) {
            var font = Minecraft.getInstance().font;

            drawBackground(graphics, context, x, y, w, 12);

            graphics.pose().pushPose();
            graphics.pose().translate(x + 4, y + 4, 0f);
            graphics.pose().scale(scale, scale, 1f);
            graphics.pose().translate(0f, -4f, 1f);

            var label = Component.literal("XP");
            graphics.drawString(font, label, 0, 0, Palette.LIGHT.decimal());

            Component value;
            int valueColor;
            if (progress >= 1f) {
                value = Component.literal("✔ Mastered");
                valueColor = Palette.POSITIVE.decimal();
            } else {
                value = Component.literal(xp + " / " + AlchemyMastery.MAX_RECIPE_XP);
                valueColor = Palette.MODERATE.decimal();
            }

            int valueW = (int) (font.width(value) * scale);
            graphics.pose().popPose();

            graphics.pose().pushPose();
            graphics.pose().translate(x + w - 4 - valueW, y + 4, 0f);
            graphics.pose().scale(scale, scale, 1f);
            graphics.pose().translate(0f, -4f, 1f);
            graphics.drawString(font, value, 0, 0, valueColor);
            graphics.pose().popPose();
        }

        private void drawBackground(GuiGraphics graphics, GuiContext context, int x, int y, int w, int h) {
            context.pushModulate(Palette.DARKEST);
            context.popModulate();
        }

        private void renderBar(GuiGraphics graphics, GuiContext context, int x, int y, int w, int h) {
            drawProgressBg(graphics, context, x, y, w, h);
            int filledW = (int) Math.max(progress > 0 ? 4 : 0, w * progress);
            if (filledW > 0) {
                drawProgressFill(graphics, context, x, y, filledW, h);
            }
        }

        private void drawProgressBg(GuiGraphics graphics, GuiContext context, int x, int y, int w, int h) {
            context.pushModulate(Palette.DARKEST);
            GuiToolkit.draw(graphics, Textures.PROGRESS, x, y, w, h);
            context.popModulate();
        }

        private void drawProgressFill(GuiGraphics graphics, GuiContext context, int x, int y, int w, int h) {
            context.pushModulate(Palette.ACCENT);
            GuiToolkit.draw(graphics, Textures.PROGRESS, x, y, w, h);
            context.popModulate();
        }

        @Override
        protected void measure() {
            int rowH = (int) (6f + 10f * scale);
            setMeasuredSize(0, rowH + 2 + 6);
        }

        @Override
        protected boolean hasOwnContent() {
            return true;
        }
    }
}