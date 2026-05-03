package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.api.alchemy.AlchemyIngredient;
import dev.obscuria.elixirum.api.alchemy.AlchemyRecipe;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RecipeDetails extends AbstractDetails {

    public RecipeDetails(AlchemyRecipe recipe) {
        super(ElixirumUI.DETAILS_RECIPE);
        addChild(new Sequence(recipe));
    }

    private static class Sequence extends Control {

        private int childWidth;

        public Sequence(AlchemyRecipe recipe) {
            super(0, 0, 0, 18, CommonComponents.EMPTY);
            recipe.base().ifPresent(this::addIngredient);
            recipe.catalyst().ifPresent(this::addIngredientWithReaction);
            recipe.inhibitor().ifPresent(this::addIngredientWithReaction);
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            renderChildren(graphics, context, mouseX, mouseY);
        }

        @Override
        protected void measure() {
            int totalWidth = 0;
            int maxHeight = 0;
            for (var child : getChildren()) {
                totalWidth += child.getMeasuredWidth();
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            this.childWidth = totalWidth;
            setMeasuredSize(totalWidth, maxHeight);
        }

        @Override
        protected void layout() {
            var offset = getX() + (getWidth() - childWidth) / 2;
            for (var child : getChildren()) {
                placeChild(child, offset, getY(),
                        child.getMeasuredWidth(),
                        child.getMeasuredHeight());
                offset += child.getMeasuredWidth();
            }
        }

        private void addIngredient(AlchemyIngredient ingredient) {
            addChild(new Ingredient(ingredient.asStack()));
        }

        private void addIngredientWithReaction(AlchemyIngredient ingredient) {
            addChild(new Reaction());
            addChild(new Ingredient(ingredient.asStack()));
        }

        private static class Ingredient extends Control {

            private final ItemStack stack;

            public Ingredient(ItemStack stack) {
                this.stack = stack;
            }

            @Override
            public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
                graphics.renderItem(stack, getX() + 1, getY() + 1);
                if (!context.isMouseOver(this, mouseX, mouseY)) return;
                GuiToolkit.draw(graphics, Textures.OUTLINE_PURPLE, this);
                AlchemyScreen.tooltipStack = stack;
            }

            @Override
            protected void measure() {
                setMeasuredSize(18, 18);
            }

            @Override
            protected boolean hasOwnContent() {
                return true;
            }
        }

        private static class Reaction extends Control {

            private static final ResourceLocation ARROW_TEXTURE = ArsElixirum.identifier("textures/gui/arrow.png");

            @Override
            public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
                context.pushModulate(Palette.DARKEST);
                graphics.pose().pushPose();
                graphics.pose().translate(getX() + getWidth() * 0.5f, getY() + getHeight() * 0.5f, 0);
                graphics.blit(ARROW_TEXTURE, -3, -8, 6, 16, 6, 16, -6, 16, 16, 16);
                graphics.pose().popPose();
                context.popModulate();
            }

            @Override
            protected void measure() {
                setMeasuredSize(6, 18);
            }

            @Override
            protected boolean hasOwnContent() {
                return true;
            }
        }
    }
}
