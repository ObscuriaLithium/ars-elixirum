package dev.obscuria.elixirum.client.screen.toolkit.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ButtonControl extends HierarchicalControl {

    public ButtonControl(Component name) {
        super(0, 0, 14, 14, name);
        this.clickAction = ClickAction.<ButtonControl>leftClick(this::onClick);
    }

    public Component getButtonName() {
        return getMessage();
    }

    public Texture pickTexture(boolean isHovered) {
        return ArsElixirumTextures.buttonGray(isHovered);
    }

    public void renderButton(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        GuiGraphicsUtil.drawShifted(graphics, pickTexture(active && isHovered), this);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (!transform.isWithinScissor()) return;
        this.isHovered = transform.isMouseOver(mouseX, mouseY);
        final var font = Minecraft.getInstance().font;

        if (active) {
            this.renderButton(graphics, transform, mouseX, mouseY);
            graphics.drawCenteredString(font, getButtonName(), (int) rect.centerX(), rect.y() + 3, -0x1);
        } else {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, 0.33f);
            this.renderButton(graphics, transform, mouseX, mouseY);
            graphics.drawCenteredString(font, getButtonName(), (int) rect.centerX(), rect.y() + 3, -0x1);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void reorganize() {}

    protected void onClick(ButtonControl self) {}
}
