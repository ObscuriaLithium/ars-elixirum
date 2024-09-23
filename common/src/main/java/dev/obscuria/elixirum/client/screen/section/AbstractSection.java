package dev.obscuria.elixirum.client.screen.section;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public abstract class AbstractSection extends AbstractButton {
    private final Consumer<AbstractSection> action;
    private final Type type;
    private boolean selected;

    protected AbstractSection(int center, Type type, Consumer<AbstractSection> action) {
        super(1, center - 11 * Type.values().length + 22 * type.ordinal(), 20, 20, type.getDisplayName());
        this.type = type;
        this.action = action;
    }

    public final Type getType() {
        return this.type;
    }

    public final void setSelected(boolean value) {
        this.selected = value;
    }

    public abstract void initTab(ElixirumScreen screen);

    @Override
    public final void onPress() {
        this.action.accept(this);
    }

    @Override
    public final void playDownSound(SoundManager manager) {
        manager.play(SimpleSoundInstance.forUI(ElixirumSounds.UI_BELL.value(), 1.0F));
    }

    @Override
    protected final void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 0.25F);
            graphics.blitSprite(ElixirumScreen.SPRITE_PANEL_PURPLE, getX(), getY(), getWidth(), getHeight());
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
        if (selected) {
            graphics.blitSprite(type.getIconSprite(), getX() + 1, getY() + 1, 18, 18);
        } else {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 0.25F);
            graphics.blitSprite(type.getIconSprite(), getX() + 1, getY() + 1, 18, 18);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }

    @Override
    protected final void updateWidgetNarration(NarrationElementOutput output) {}

    public enum Type {
        RECENT("icon/recent"),
        COLLECTION("icon/collection"),
        COMPENDIUM("icon/compendium");

        private final ResourceLocation icon;

        Type(String iconPath) {
            this.icon = Elixirum.key(iconPath);
        }

        public final ResourceLocation getIconSprite() {
            return this.icon;
        }

        public final Component getDisplayName() {
            return Component.translatable("tab.elixirum." + this.name().toLowerCase());
        }
    }
}
