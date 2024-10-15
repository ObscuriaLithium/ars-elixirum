package dev.obscuria.elixirum.client.screen.section.collection;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.container.GridContainer;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.widget.AbstractElixirDisplay;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.world.item.ItemStack;

final class SubElixirsGrid extends GridContainer
{
    public SubElixirsGrid()
    {
        this.update();
    }

    public void update()
    {
        this.children().clear();
        for (var holder : ClientAlchemy.getProfile().getCollection())
        {
            this.addChild(new Entry(holder)
                    .setClickSound(ElixirumSounds.UI_CLICK_2)
                    .setClickAction(ClickAction.<Entry>left(widget -> {
                        RootCollection.select(widget.getHolder());
                        return true;
                    })));
        }
    }

    static final class Entry extends AbstractElixirDisplay
    {
        private final ElixirHolder holder;

        public Entry(ElixirHolder holder)
        {
            super(holder.getCachedStack().orElse(ItemStack.EMPTY));
            this.holder = holder;
        }

        public ElixirHolder getHolder()
        {
            return this.holder;
        }

        @Override
        protected boolean isSelected()
        {
            return RootCollection.getSelectedHolder().map(holder::isSame).orElse(false);
        }
    }
}
