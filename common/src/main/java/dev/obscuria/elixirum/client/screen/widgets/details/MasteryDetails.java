package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.SpacingControl;
import dev.obscuria.elixirum.common.alchemy.recipes.AlchemyRecipe;
import dev.obscuria.fragmentum.util.color.Colors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;

public class MasteryDetails extends AbstractDetails {

    public MasteryDetails(CachedElixir elixir) {
        super(ElixirumUI.DETAILS_MASTERY);
        this.addChild(new ParagraphControl(ElixirumUI.MASTERY_HINT));
        this.addChild(new SpacingControl(6));
        this.addChild(new Progress(elixir.recipe()));
    }

    private static class Progress extends HierarchicalControl {

        private final float progress;

        public Progress(AlchemyRecipe recipe) {
            super(0, 0, 10, 4, CommonComponents.EMPTY);
            this.progress = ClientAlchemy.localProfile().mastery().getRecipeXp(recipe.getUuid()) / 100f;
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            var width = (int) Math.max(4, rect.width() * progress);
            GuiGraphicsUtil.setShaderColor(Colors.argbOf(0x40384A));
            GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.PROGRESS, this);
            GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.ACCENT);
            GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.PROGRESS, rect.x(), rect.y(), width, rect.height());
            GuiGraphicsUtil.resetShaderColor();
        }

        @Override
        public void reorganize() {}
    }
}
