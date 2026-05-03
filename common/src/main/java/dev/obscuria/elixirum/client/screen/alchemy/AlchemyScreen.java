package dev.obscuria.elixirum.client.screen.alchemy;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
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
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.Music;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class AlchemyScreen extends Screen {

    public static final ARGB COLOR_BACKGROUND = Colors.argbOf(0xbf13000d);
    public static final ARGB COLOR_PANEL = Colors.argbOf(0xff2b1d2a);
    public static final ARGB COLOR_GRADIENT_TOP = Colors.argbOf(0xff82747c);
    public static final ARGB COLOR_GRADIENT_BOTTOM = Colors.argbOf(0xff2e2029);
    public static final Music BACKGROUND_MUSIC;

    private static final int TAB_PANEL_WIDTH = 23;

    public static AlchemyPage lastPage = AlchemyPage.RECENTLY_BREWED;
    public static @Nullable Supplier<List<FormattedCharSequence>> tooltipProvider;
    public static @Nullable net.minecraft.world.item.ItemStack tooltipStack;
    public static @Nullable Tooltip tooltip;

    protected AlchemyScreen(AlchemyPage page) {
        super(Component.literal(ArsElixirum.DISPLAY_NAME));
        lastPage = page;
    }

    public int left(int shift) {
        return TAB_PANEL_WIDTH + shift;
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
        return width - TAB_PANEL_WIDTH + shift;
    }

    public int height(int shift) {
        return height + shift;
    }

    public int center() {
        return left(width(0) / 2) + 2;
    }

    @Override
    protected void init() {
        for (int i = 0; i < AlchemyPage.values().length; i++) {
            int buttonY = height / 2 - (22 * AlchemyPage.values().length) / 2 + 22 * i;
            addChild(new SectionButton(1, buttonY, AlchemyPage.values()[i]));
        }
//        addChild(new GenerationStatus(
//                ClientAlchemy.INSTANCE.essences().generationResult(),
//                ClientAlchemy.INSTANCE.ingredients().generationResult(),
//                1, 1));
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        graphics.fill(0, 0, width, height, COLOR_BACKGROUND.decimal());
        graphics.fill(0, 0, TAB_PANEL_WIDTH - 1, height, COLOR_PANEL.decimal());
        graphics.fillGradient(TAB_PANEL_WIDTH - 1, 0, TAB_PANEL_WIDTH, height,
                COLOR_GRADIENT_TOP.decimal(), COLOR_GRADIENT_BOTTOM.decimal());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        var font = Minecraft.getInstance().font;
        if (tooltipProvider != null) {
            graphics.renderTooltip(font, tooltipProvider.get(), mouseX, mouseY);
            tooltipProvider = null;
        }
        if (tooltipStack != null) {
            graphics.renderTooltip(font, tooltipStack, mouseX, mouseY);
            tooltipStack = null;
        }
        if (tooltip != null) {
            var positioner = DefaultTooltipPositioner.INSTANCE;
            graphics.renderTooltip(font, tooltip.toCharSequence(Minecraft.getInstance()), positioner, mouseX, mouseY);
            tooltip = null;
        }
    }

    @Override
    public void tick() {
        for (var child : children()) {
            if (!(child instanceof Control control)) continue;
            control.tick();
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

    public <T extends GuiEventListener & Renderable & NarratableEntry> T addChild(T widget) {
        return super.addRenderableWidget(widget);
    }

    private static class SectionButton extends AbstractButton {

        private final AlchemyPage page;

        SectionButton(int x, int y, AlchemyPage page) {
            super(x, y, 20, 20, CommonComponents.EMPTY);
            this.page = page;
        }

        public boolean isSelected() {
            return lastPage == page;
        }

        @Override
        public void onPress() {
            if (!isSelected()) page.open();
        }

        @Override
        public void playDownSound(SoundManager handler) {
            handler.play(SimpleSoundInstance.forUI(ElixirumSounds.UI_BELL, 1.0F));
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput out) {
            out.add(NarratedElementType.TITLE, page.displayName());
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            float alpha = isSelected() ? 1f : isHoveredOrFocused() ? 0.5f : 0.25f;
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
            graphics.blit(page.icon(), getX() + 1, getY() + 1, 0f, 0f, 18, 18, 18, 18);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }
    }

    static {
        BACKGROUND_MUSIC = new Music(ElixirumSounds.MUSIC_ELIXIRUM.holder(), 600, 1200, false);
    }
}
