package dev.obscuria.elixirum.client.screen.widgets.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import dev.obscuria.fragmentum.util.color.ARGB;
import dev.obscuria.fragmentum.util.color.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.Music;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractPage extends Screen {

    public static final ARGB COLOR_BACKGROUND = Colors.argbOf(0xbf13000d);
    public static final ARGB COLOR_PANEL = Colors.argbOf(0xff2b1d2a);
    public static final ARGB COLOR_GRADIENT_TOP = Colors.argbOf(0xff82747c);
    public static final ARGB COLOR_GRADIENT_BOTTOM = Colors.argbOf(0xff2e2029);
    public static final Music BACKGROUND_MUSIC;

    public static PageKind lastKind = PageKind.RECENTLY_BREWED;
    public static @Nullable Supplier<List<FormattedCharSequence>> tooltipProvider;
    public static @Nullable ItemStack tooltipStack;
    public static @Nullable Tooltip tooltip;

    public AbstractPage(PageKind kind) {
        super(Component.literal(ArsElixirum.DISPLAY_NAME));
        lastKind = kind;
    }

    public int left(int shift) {
        return 23 + shift;
    }

    public int right(int shift) {
        return width + shift;
    }

    public int top(int shift) {
        return shift;
    }

    public int bottom(int shift) {
        return height + shift;
    }

    public int width(int shift) {
        return width - 23 + shift;
    }

    public int height(int shift) {
        return height + shift;
    }

    public int center() {
        return left(width(0) / 2) + 2;
    }

    public <T extends GuiEventListener & Renderable & NarratableEntry> T addChild(T widget) {
        return super.addRenderableWidget(widget);
    }

    @Override
    protected void init() {
        for (var i = 0; i < PageKind.values().length; i++) {
            final var buttonY = height / 2 - (22 * PageKind.values().length) / 2 + 22 * i;
            this.addChild(new SectionButton(1, buttonY, PageKind.values()[i]));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public Music getBackgroundMusic() {
        return BACKGROUND_MUSIC;
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), COLOR_BACKGROUND.decimal());
        graphics.fill(0, 0, 22, height, COLOR_PANEL.decimal());
        graphics.fillGradient(22, 0, 23, height, COLOR_GRADIENT_TOP.decimal(), COLOR_GRADIENT_BOTTOM.decimal());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        if (tooltipProvider != null) {
            graphics.renderTooltip(Minecraft.getInstance().font, tooltipProvider.get(), mouseX, mouseY);
            tooltipProvider = null;
        }

        if (tooltipStack != null) {
            graphics.renderTooltip(Minecraft.getInstance().font, tooltipStack, mouseX, mouseY);
            tooltipStack = null;
        }

        if (tooltip != null) {
            final var minecraft = Minecraft.getInstance();
            final var positioner = DefaultTooltipPositioner.INSTANCE;
            graphics.renderTooltip(minecraft.font, tooltip.toCharSequence(minecraft), positioner, mouseX, mouseY);
            tooltip = null;
        }
    }

    @Override
    public void tick() {
        for (var child : children()) {
            if (!(child instanceof HierarchicalControl node)) continue;
            node.tick();
        }
    }

    private static class SectionButton extends AbstractButton {

        private final PageKind page;

        public SectionButton(int x, int y, PageKind page) {
            super(x, y, 20, 20, CommonComponents.EMPTY);
            this.page = page;
        }

        public boolean isSelected() {
            return lastKind == page;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

        @Override
        public void onPress() {
            if (isSelected()) return;
            this.page.open();
        }

        @Override
        public void playDownSound(SoundManager handler) {
            handler.play(SimpleSoundInstance.forUI(ElixirumSounds.UI_BELL, 1.0F));
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            if (isHovered) {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.25f);
                graphics.blit(page.icon(), getX() + 1, getY() + 1, 0f, 0f, 18, 18, 18, 18);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
            }
            if (isSelected()) {
                graphics.blit(page.icon(), getX() + 1, getY() + 1, 0f, 0f, 18, 18, 18, 18);
            } else {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.25f);
                graphics.blit(page.icon(), getX() + 1, getY() + 1, 0f, 0f, 18, 18, 18, 18);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
            }
        }
    }

    static {
        BACKGROUND_MUSIC = new Music(ElixirumSounds.MUSIC_ELIXIRUM.holder(), 600, 1200, false);
    }
}
