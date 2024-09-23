package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.container.GridContainer;
import dev.obscuria.elixirum.client.screen.widget.AbstractElixirDisplay;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

final class SubElixirsGrid extends GridContainer {

    public SubElixirsGrid() {
        for (var elixir : ClientAlchemy.getRecentElixirs()) {
            this.addChild(new Entry(elixir)
                    .setClickSound(ElixirumSounds.UI_CLICK_2.holder())
                    .setClickAction(ClickAction.<Entry>left(widget -> {
                        RootRecent.select(widget.getElixir());
                        return true;
                    })));
        }
    }

    static final class Entry extends AbstractElixirDisplay {
        private static final ResourceLocation CHECK_MARK = Elixirum.key("icon/check_mark");
        private final ClientAlchemy.RecentElixir elixir;

        public Entry(ClientAlchemy.RecentElixir elixir) {
            super(elixir.stack());
            this.elixir = elixir;
        }

        public ClientAlchemy.RecentElixir getElixir() {
            return this.elixir;
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            super.render(graphics, transform, mouseX, mouseY);
            if (!elixir.saved().get()) return;
            graphics.blitSprite(CHECK_MARK, getRight() - 6, getBottom() - 6, 6, 6);
        }

        @Override
        protected boolean isSelected() {
            return RootRecent.getSelected().map(entry -> entry == this.elixir).orElse(false);
        }
    }
}
