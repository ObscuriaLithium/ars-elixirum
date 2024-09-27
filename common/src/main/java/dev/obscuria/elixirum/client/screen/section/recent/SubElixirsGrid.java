package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.container.GridContainer;
import dev.obscuria.elixirum.client.screen.widget.AbstractElixirDisplay;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

final class SubElixirsGrid extends GridContainer {

    public SubElixirsGrid() {
        for (var holder : ClientAlchemy.getCache().getRecentElixirs()) {
            this.addChild(new Entry(holder)
                    .setClickSound(ElixirumSounds.UI_CLICK_2)
                    .setClickAction(ClickAction.<Entry>left(widget -> {
                        RootRecent.select(widget.getHolder());
                        return true;
                    })));
        }
    }

    static final class Entry extends AbstractElixirDisplay {
        private static final ResourceLocation CHECK_MARK = Elixirum.key("icon/check_mark");
        private final ElixirHolder holder;

        public Entry(ElixirHolder holder) {
            super(holder.getCachedStack().orElse(ItemStack.EMPTY));
            this.holder = holder;
        }

        public ElixirHolder getHolder() {
            return this.holder;
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            super.render(graphics, transform, mouseX, mouseY);
            if (!ClientAlchemy.getProfile().isOnCollection(holder.getRecipe())) return;
            graphics.blitSprite(CHECK_MARK, getRight() - 6, getBottom() - 6, 6, 6);
        }

        @Override
        protected boolean isSelected() {
            return RootRecent.getSelectedHolder().map(holder::isSame).orElse(false);
        }
    }
}
