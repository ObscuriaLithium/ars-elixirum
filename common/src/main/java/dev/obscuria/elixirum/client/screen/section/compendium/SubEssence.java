package dev.obscuria.elixirum.client.screen.section.compendium;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.screen.ElixirumPalette;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

final class SubEssence extends HierarchicalWidget {
    private final Holder<Essence> essenceHolder;
    private final int index;
    private final boolean light;

    public SubEssence(Holder<Essence> essenceHolder, int index, boolean light) {
        super(0, 0, 0, 22, Component.empty());
        this.essenceHolder = essenceHolder;
        this.index = index;
        this.light = light;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        if (!transform.isWithinScissor()) return;

        RenderSystem.enableBlend();
        if (light) {
            RenderSystem.setShaderColor(1, 1, 1, 0.1f);
            graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_LIGHT, getX(), getY(), getWidth(), getHeight());
        } else {
            RenderSystem.setShaderColor(1, 1, 1, 0.2f);
            graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_DARK, getX(), getY(), getWidth(), getHeight());
        }
        RenderSystem.setShaderColor(1, 1, 1, 0.4f);
        graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_DARK, getX() + 2, getY() + 2, 18, 18);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        final var font = Minecraft.getInstance().font;
        final var textures = Minecraft.getInstance().getMobEffectTextures();
        final var mobEffect = essenceHolder.value().effectHolder();
        graphics.drawCenteredString(font, String.valueOf(index), getX() + 11, getY() + 7, ElixirumPalette.LIGHT);
        graphics.blit(getX() + 22, getY() + 2, 0, 18, 18, textures.get(mobEffect));
        graphics.drawString(font, essenceHolder.value().getName(), getX() + 44, getY() + 7, ElixirumPalette.LIGHT);

        if (transform.isMouseOver(mouseX, mouseY)) {
            graphics.blitSprite(ElixirumScreen.SPRITE_OUTLINE_PURPLE, getX(), getY(), getWidth(), getHeight());
            ElixirumScreen.tooltipProvider = this::getCustomTooltip;
        }
    }

    @Override
    protected void reorganize() {}

    @Contract(" -> new")
    private @Unmodifiable List<Component> getCustomTooltip() {
        final var essence = essenceHolder.value();
        return List.of(
                essence.getName(),
                Component.literal("Required Ingredients: " + essence.requiredIngredients()));
    }
}
