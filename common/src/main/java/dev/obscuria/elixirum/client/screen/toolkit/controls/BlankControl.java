package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;

public class BlankControl extends HierarchicalControl {

    public BlankControl() {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {}

    @Override
    public void reorganize() {}
}
