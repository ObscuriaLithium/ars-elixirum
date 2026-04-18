package dev.obscuria.elixirum.client.screen.widgets.pages.recent;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.widgets.AbstractElixirWidget;
import net.minecraft.client.gui.GuiGraphics;

class RecentElixirWidget extends AbstractElixirWidget {

    protected RecentElixirWidget(SelectionState<CachedElixir> selection, CachedElixir elixir) {
        super(selection, elixir);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        super.render(graphics, transform, mouseX, mouseY);
        if (!elixir.isInCollection()) return;
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 200);
        GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.CHECK_MARK, rect.right() - 6, rect.bottom() - 6, 6, 6);
        graphics.pose().popPose();
    }
}