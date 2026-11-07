package dev.obscuria.elixirum.client.screen.alchemy.pages.recent;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.alchemy.controls.ElixirControl;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import net.minecraft.client.gui.GuiGraphics;

class RecentElixirControl extends ElixirControl {

    protected RecentElixirControl(Selection<CachedElixir> selection, CachedElixir elixir) {
        super(selection, elixir);
    }

    @Override
    protected void renderElixir(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
        super.renderElixir(graphics, context, mouseX, mouseY);
        if (!elixir.isInCollection()) return;
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 200);
        GuiToolkit.draw(graphics, Textures.CHECK_MARK,
                getX() + getWidth() - 6,
                getY() + getHeight() - 6,
                6, 6);
        graphics.pose().popPose();
    }
}
