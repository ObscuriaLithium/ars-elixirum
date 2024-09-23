package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.container.GridContainer;
import dev.obscuria.elixirum.client.screen.container.SpoilerContainer;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class RecipeDisplay extends SpoilerContainer {
    private final ElixirRecipe recipe;

    public RecipeDisplay(ElixirRecipe recipe) {
        super(Component.literal("Recipe"));
        this.setExpanded(true);
        this.recipe = recipe;
        final var grid = addChild(new GridContainer());
        for (var item : recipe.ingredients())
            grid.addChild(new Ingredient(item.getDefaultInstance()));
    }

    private static final class Ingredient extends HierarchicalWidget {
        private final ItemStack stack;

        private Ingredient(ItemStack stack) {
            super(0, 0, 18, 18, Component.empty());
            this.stack = stack;
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            if (!transform.isWithinScissor()) return;
            ElixirumScreen.debugRenderer(this, graphics, transform, mouseX, mouseY);
            graphics.renderFakeItem(stack, getX() + 1, getY() + 1);
            if (transform.isMouseOver(mouseX, mouseY)) {
                graphics.blitSprite(ElixirumScreen.SPRITE_OUTLINE_PURPLE, getX(), getY(), getWidth(), getHeight());
                ElixirumScreen.tooltipProvider = this::getCustomTooltip;
            }
        }

        @Override
        protected void reorganize() {}

        private List<Component> getCustomTooltip() {
            return Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
        }
    }
}
