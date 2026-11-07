package dev.obscuria.elixirum.client.screen.alchemy;

import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.common.alchemy.Diff;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class GenerationStatus extends AbstractWidget {

    private final Diff essences;
    private final Diff ingredients;

    public GenerationStatus(Diff essences, Diff ingredients, int x, int y) {
        super(x, y, 16, 16, CommonComponents.EMPTY);
        this.essences = essences;
        this.ingredients = ingredients;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        GuiToolkit.draw(graphics, Textures.buttonGray(isHovered), getX(), getY(), width, height);
        if (isHovered) AlchemyScreen.tooltip = Tooltip.create(Component.empty()
                .append(Component.literal("Alchemy Environment Status:"))
                .append(CommonComponents.NEW_LINE)
                .append(Component.literal("\nAdded essences: " + essences.added().count()))
                .append(Component.literal("\nRemoved essences: " + essences.removed().count()))
                .append(Component.literal("\nUpdated essences: " + essences.updated().count()))
                .append(CommonComponents.NEW_LINE)
                .append(Component.literal("\nAdded ingredients: " + ingredients.added().count()))
                .append(Component.literal("\nRemoved ingredients: " + ingredients.removed().count()))
                .append(Component.literal("\nUpdated ingredients: " + ingredients.updated().count())));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}
