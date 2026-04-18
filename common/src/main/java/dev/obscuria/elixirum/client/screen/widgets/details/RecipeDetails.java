package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.common.alchemy.recipe.AlchemyRecipe;
import dev.obscuria.fragmentum.util.color.Colors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class RecipeDetails extends AbstractDetails {

    public RecipeDetails(AlchemyRecipe recipe) {
        super(Component.literal("Recipe"));
        this.addChild(new Sequence(recipe));
    }

    private static class Sequence extends HierarchicalControl {

        public Sequence(AlchemyRecipe recipe) {
            super(0, 0, 0, 18, CommonComponents.EMPTY);
            recipe.foundation().ifPresent(it -> this.addChild(new Ingredient(it.asStack())));
            recipe.catalyst().ifPresent(it -> {
                this.addChild(new Reaction());
                this.addChild(new Ingredient(it.asStack()));
            });
            recipe.stabilizer().ifPresent(it -> {
                this.addChild(new Reaction());
                this.addChild(new Ingredient(it.asStack()));
            });
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            this.renderChildren(graphics, transform, mouseX, mouseY);
        }

        @Override
        public void reorganize() {
            var childWidth = listChildren().mapToInt(it -> it.rect.width()).sum();
            var childX = (int) rect.centerX() - childWidth / 2;
            for (var child : this.listChildren().toList()) {
                child.setY(rect.y());
                child.setX(childX);
                childX += child.rect.width();
            }
        }

        private static class Ingredient extends HierarchicalControl {

            private final ItemStack stack;

            public Ingredient(ItemStack stack) {
                super(0, 0, 18, 18, CommonComponents.EMPTY);
                this.stack = stack;
            }

            @Override
            public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
                graphics.renderItem(stack, rect.x() + 1, rect.y() + 1);
                if (!transform.isMouseOver(mouseX, mouseY)) return;
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_PURPLE, this);
                AbstractPage.tooltipStack = stack;
            }

            @Override
            public void reorganize() {}
        }

        private static class Reaction extends HierarchicalControl {

            private static final ResourceLocation ARROW_TEXTURE = ArsElixirum.identifier("textures/gui/arrow.png");

            public Reaction() {
                super(0, 0, 8, 18, CommonComponents.EMPTY);
            }

            @Override
            public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
                GuiGraphicsUtil.setShaderColor(Colors.argbOf(0xFF40384A));
                graphics.pose().pushPose();
                graphics.pose().translate(rect.centerX(), rect.centerY(), 0);
                graphics.blit(ARROW_TEXTURE, -3, -8, 6, 16, 6, 16, -6, 16, 16, 16);
                graphics.pose().popPose();
                GuiGraphicsUtil.resetShaderColor();
            }

            @Override
            public void reorganize() {}
        }
    }
}
