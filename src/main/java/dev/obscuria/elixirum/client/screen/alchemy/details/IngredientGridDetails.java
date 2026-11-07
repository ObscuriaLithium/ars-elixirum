package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;

public class IngredientGridDetails extends AbstractDetails {

    public IngredientGridDetails(Essence essence) {
        super(ElixirumUI.DETAILS_INGREDIENTS);
        var grid = new GridContainer(1, 6, 0, 0, 0);
        var entries = new ArrayList<Entry>();
        ClientAlchemy.INSTANCE.ingredients().forEach((item, properties) -> {
            if (!properties.essences().contains(essence)) return;
            entries.add(new Entry(
                    item.getDefaultInstance(),
                    DiscoverySystem.isAnyEssenceKnown(ClientAlchemy.INSTANCE, ClientAlchemy.localProfile(), item)));
        });
        entries.stream().sorted().forEach(grid::addChild);
        this.addChild(grid);
    }

    private static class Entry extends Control implements Comparable<Entry> {

        private static final Comparator<Entry> COMPARATOR;
        private final ItemStack stack;
        private final boolean discovered;

        public Entry(ItemStack stack, boolean discovered) {
            super(0, 0, 16, 16, stack.getDisplayName());
            this.stack = stack;
            this.discovered = discovered;
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            if (!context.isVisible(this)) return;

            if (!discovered) context.pushModulate(Palette.BLACK);
            graphics.renderItem(stack, getX(), getY());
            if (!discovered) context.popModulate();

            if (discovered && context.isMouseOver(this, mouseX, mouseY)) {
                GuiToolkit.draw(graphics, Textures.OUTLINE_PURPLE, this, -1, -1, 2, 2);
                AlchemyScreen.tooltipStack = stack;
            }
        }

        @Override
        public int compareTo(Entry other) {
            return COMPARATOR.compare(this, other);
        }

        static {
            COMPARATOR = Comparator
                    .<Entry, Boolean>comparing(it -> it.discovered, Comparator.reverseOrder())
                    .thenComparing(it -> it.stack.getDisplayName().getString());
        }
    }
}
