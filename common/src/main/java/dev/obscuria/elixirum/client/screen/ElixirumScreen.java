package dev.obscuria.elixirum.client.screen;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.section.AbstractSection;
import dev.obscuria.elixirum.client.screen.section.collection.RootCollection;
import dev.obscuria.elixirum.client.screen.section.compendium.RootCompendium;
import dev.obscuria.elixirum.client.screen.section.recent.RootRecent;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class ElixirumScreen extends Screen {
    public static final Music MUSIC;
    public static final ResourceLocation SPRITE_PANEL = Elixirum.key("panel");
    public static final ResourceLocation SPRITE_PANEL_DARK = Elixirum.key("panel/dark");
    public static final ResourceLocation SPRITE_PANEL_LIGHT = Elixirum.key("panel/light");
    public static final ResourceLocation SPRITE_PANEL_PURPLE = Elixirum.key("panel/purple");
    public static final ResourceLocation SPRITE_OUTLINE_WHITE = Elixirum.key("outline/white");
    public static final ResourceLocation SPRITE_OUTLINE_PURPLE = Elixirum.key("outline/purple");
    public static @Nullable TooltipProvider tooltipProvider;
    private static AbstractSection.Type selectedSection;

    public ElixirumScreen() {
        super(Component.literal(Elixirum.DISPLAY_NAME));
    }

    public static void debugRenderer(AbstractWidget widget, GuiGraphics graphics, GlobalTransform rect, int mouseX, int mouseY) {
        if (true) return;
        graphics.renderOutline(
                widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(),
                rect.isMouseOver(mouseX, mouseY) ? 0xFF006FDE : 0xFFDE6F00);
    }

    public int left(int offset) {
        return 23 + offset;
    }

    public int right(int offset) {
        return width + offset;
    }

    public int top(int offset) {
        return offset;
    }

    public int bottom(int offset) {
        return height + offset;
    }

    public int width(int offset) {
        return width - 23 + offset;
    }

    public int height(int offset) {
        return height + offset;
    }

    @Override
    public Music getBackgroundMusic() {
        return MUSIC;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        return super.addRenderableWidget(widget);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (tooltipProvider != null) {
            graphics.renderTooltip(
                    Minecraft.getInstance().font, tooltipProvider.getTooltip(),
                    Optional.empty(), mouseX, mouseY);
            tooltipProvider = null;
        }
    }

    @Override
    public void tick() {
        this.children().stream()
                .filter(HierarchicalWidget.class::isInstance)
                .map(HierarchicalWidget.class::cast)
                .forEach(HierarchicalWidget::tick);
    }

    @Override
    protected void renderMenuBackground(GuiGraphics graphics) {
        super.renderMenuBackground(graphics);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), 0xBF090013);
        graphics.fill(0, 0, 22, height, 0xFF231D2B);
        graphics.fillGradient(22, 0, 23, height, 0xFF7A7482, 0xFF26202E);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new RootRecent(height / 2, this::onTabPressed));
        this.addRenderableWidget(new RootCollection(height / 2, this::onTabPressed));
        this.addRenderableWidget(new RootCompendium(height / 2, this::onTabPressed));
        this.findTab(selectedSection).ifPresent(section -> {
            section.initTab(this);
            section.setSelected(true);
        });
    }

    private Optional<AbstractSection> findTab(AbstractSection.Type type) {
        return this.children().stream()
                .filter(AbstractSection.class::isInstance)
                .map(AbstractSection.class::cast)
                .filter(tab -> tab.getType() == type)
                .findFirst();
    }

    private void onTabPressed(AbstractSection tab) {
        if (selectedSection == tab.getType()) return;
        selectedSection = tab.getType();
        this.rebuildWidgets();
    }

    static {
        MUSIC = new Music(ElixirumSounds.MUSIC_ELIXIRUM.holder(), 600, 1200, false);
        selectedSection = ClientAlchemy.getProfile().isEmpty()
                ? AbstractSection.Type.COMPENDIUM
                : AbstractSection.Type.RECENT;
    }

    @FunctionalInterface
    public interface TooltipProvider {

        List<Component> getTooltip();
    }
}
