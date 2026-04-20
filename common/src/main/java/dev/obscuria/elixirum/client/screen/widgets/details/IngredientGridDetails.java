package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;

public class IngredientGridDetails extends AbstractDetails {

    public IngredientGridDetails(Essence essence) {
        super(Component.literal("Ingredients"));
        var grid = new GridContainer(1, 6, 0, 0, 0);
        var entries = new ArrayList<Entry>();
        for (var entry : ClientAlchemy.INSTANCE.ingredients().asMapView().entrySet()) {
            if (!entry.getValue().essences().contains(essence)) continue;
            var item = entry.getKey();
            var profile = ClientAlchemy.INSTANCE.localProfile();
            var properties = ClientAlchemy.INSTANCE.ingredients().propertiesOf(item);
            entries.add(new Entry(
                    entry.getKey().getDefaultInstance(),
                    properties.isAnyDiscovered(item, profile)));
        }
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

            if (transform.isMouseOver(mouseX, mouseY)) {
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_PURPLE, this, -1, -1, 2, 2);
                if (discovered) AbstractPage.tooltipStack = stack;
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
