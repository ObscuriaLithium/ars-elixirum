package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
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
            var profile = ClientAlchemy.INSTANCE.localProfile();
            entries.add(new Entry(
                    item.getDefaultInstance(),
                    DiscoverySystem.isAnyEssenceKnown(ClientAlchemy.INSTANCE, profile, item)));
        });
        entries.stream().sorted().forEach(grid::addChild);
        this.addChild(grid);
    }

    private static class Entry extends HierarchicalControl implements Comparable<Entry> {

        private static final Comparator<Entry> COMPARATOR;
        private final ItemStack stack;
        private final boolean discovered;

        public Entry(ItemStack stack, boolean discovered) {
            super(0, 0, 16, 16, stack.getDisplayName());
            this.stack = stack;
            this.discovered = discovered;
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            if (!transform.isWithinScissor()) return;

            if (!discovered) GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.BLACK);
            graphics.renderItem(stack, rect.x(), rect.y());
            if (!discovered) GuiGraphicsUtil.resetShaderColor();

            if (discovered && transform.isMouseOver(mouseX, mouseY)) {
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_PURPLE, this, -1, -1, 2, 2);
                AbstractPage.tooltipStack = stack;
            }
        }

        @Override
        public void reorganize() {}

        @Override
        public int compareTo(IngredientGridDetails.Entry other) {
            return COMPARATOR.compare(this, other);
        }

        static {
            COMPARATOR = Comparator
                    .<Entry, Boolean>comparing(it -> it.discovered, Comparator.reverseOrder())
                    .thenComparing(it -> it.stack.getDisplayName().getString());
        }
    }
}
