package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;

public final class EffectDisplay extends HierarchicalWidget {
    private final ElixirEffect effect;
    private MultiLineLabel label;

    public EffectDisplay(ElixirEffect effect) {
        super(0, 0, 0, 0, Component.empty());
        this.setUpdateFlags(UPDATE_BY_WIDTH);
        this.effect = effect;
        this.label = MultiLineLabel.EMPTY;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (!transform.isWithinScissor()) return;
        ElixirumScreen.debugRenderer(this, graphics, transform, mouseX, mouseY);
        final var font = Minecraft.getInstance().font;
        final var textures = Minecraft.getInstance().getMobEffectTextures();
        final var mobEffect = effect.getEssence().effectHolder();
        final var isSmall = getHeight() <= 20;
        graphics.blit(getX(), getY() + getHeight() / 2 - 9, 0, 18, 18, textures.get(mobEffect));

        graphics.pose().pushPose();
        graphics.pose().translate(getX() + 20, getY() + (isSmall ? 3 : 2), 0);
        graphics.pose().scale(0.75f, 0.75f, 0.75f);
        label.renderLeftAligned(graphics, 0, 0, 9, 0xFFCFCFCF);
        graphics.pose().popPose();

        graphics.pose().pushPose();
        graphics.pose().translate(getX() + 20, getBottom() - (isSmall ? 9 : 8), 0);
        graphics.pose().scale(0.75f, 0.75f, 0.75f);
        graphics.drawString(font, effect.getStatusOrDuration(20f), 0, 0, 0xFF7F7F7F);
        graphics.pose().popPose();
    }

    @Override
    protected void reorganize() {
        final var font = Minecraft.getInstance().font;
        final var effectName = effect.getDisplayName();
        this.label = MultiLineLabel.create(font, effectName, getWidth() - 20);
        this.setHeight(Math.max(20, 2 + (int) Math.ceil(7.5f * label.getLineCount() - 1) + 8));
    }
}
