package dev.obscuria.elixirum.client.screen.alchemy.pages.collection;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.containers.AbstractDetailsPage;
import dev.obscuria.elixirum.client.screen.alchemy.details.*;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.FootnoteControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.containers.SplitContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.fragmentum.util.signal.Signal0;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class CollectionDetails extends AbstractDetailsPage<CachedElixir> {

    private final Selection<TabType> tabSelection;
    private final Signal0 styleChanged;

    protected CollectionDetails(
            Selection<CachedElixir> selection, Signal0 styleChanged,
            int x, int y, int width, int height
    ) {
        super(selection, x, y, width, height);
        this.tabSelection = new Selection<>(TabType.INFO);
        this.tabSelection.addListener(tab -> this.updateDetails());
        this.styleChanged = styleChanged;
        this.header.addChild(Util.make(new SplitContainer(0.5f, 0, false), it -> {
            it.addChild(new TabButton(tabSelection, TabType.INFO));
            it.addChild(new TabButton(tabSelection, TabType.STYLE));
        }));
    }

    @Override
    protected Component getPlaceholder() {
        return CommonComponents.EMPTY;
    }

    @Override
    protected Component getDisplayName(CachedElixir target) {
        return target.displayName();
    }

    @Override
    protected boolean isEmpty(CachedElixir target) {
        return target.isEmpty();
    }

    @Override
    protected void rebuild(CachedElixir target) {
        if (target.isEmpty()) return;
        if (tabSelection.get() == TabType.INFO) {
            this.content.addChild(new ContentDetails(ArsElixirumAPI.getElixirContents(target.get())));
            if (!target.recipe().isEmpty()) this.content.addChild(new RecipeDetails(target.recipe()));
            this.content.addChild(new AssessmentDetails(target));
            this.content.addChild(new MasteryDetails(target));
            this.content.addChild(new ScoreDetails(target));
            this.content.addChild(new ActionDetails(target));
        } else {
            this.content.addChild(new FootnoteControl(ElixirumUI.STYLE_HINT));
            this.content.addChild(new StyleCapDetails(target, styleChanged));
            this.content.addChild(new StyleShapeDetails(target, styleChanged));
            this.content.addChild(new StyleChromaDetails(target, styleChanged));
        }
    }

    enum TabType {
        INFO,
        STYLE;

        public Component displayName() {
            return Component.translatable("ui.elixirum.details.tab." + name().toLowerCase());
        }
    }

    static class TabButton extends ButtonControl {

        private final Selection<TabType> selection;
        private final TabType tabType;

        public TabButton(Selection<TabType> selection, TabType tabType) {
            super(tabType.displayName());
            this.selection = selection;
            this.tabType = tabType;
        }

        public boolean isSelected() {
            return this.tabType == selection.get();
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            if (!context.isVisible(this)) return;
            this.isHovered = context.isMouseOver(this, mouseX, mouseY);
            var font = Minecraft.getInstance().font;
            if (isSelected()) {
                RenderSystem.enableBlend();
                GuiToolkit.draw(graphics, Textures.SHINE_X64, this, -4, -4, 8, 8);
                RenderSystem.disableBlend();
            }
            var color = isSelected() ? Palette.WHITE
                    : isHovered ? Palette.LIGHT
                    : Palette.MODERATE;
            graphics.drawCenteredString(font, pickButtonName(),
                    (int) (getX() + getWidth() * 0.5f),
                    getY() + 3, color.decimal());
        }

        @Override
        protected void onClick() {
            if (isSelected()) return;
            this.selection.set(tabType);
        }
    }
}