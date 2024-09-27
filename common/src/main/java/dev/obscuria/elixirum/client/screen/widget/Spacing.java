package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class Spacing extends HierarchicalWidget {

    public Spacing(int width, int height) {
        super(0, 0, width, height, Component.empty());
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {}

    @Override
    protected void reorganize() {}
}
