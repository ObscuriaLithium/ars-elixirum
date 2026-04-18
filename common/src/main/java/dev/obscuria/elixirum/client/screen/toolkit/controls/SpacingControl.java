package dev.obscuria.elixirum.client.screen.toolkit.controls;

import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;

public final class SpacingControl extends HierarchicalControl {

    public SpacingControl(int height) {
        super(0, 0, 0, height, CommonComponents.EMPTY);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {}

    @Override
    public void reorganize() {}
}
