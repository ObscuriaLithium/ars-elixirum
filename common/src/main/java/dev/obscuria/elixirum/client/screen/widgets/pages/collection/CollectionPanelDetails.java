package dev.obscuria.elixirum.client.screen.widgets.pages.collection;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.containers.SplitContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import dev.obscuria.elixirum.client.screen.widgets.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widgets.details.*;
import dev.obscuria.elixirum.helpers.ContentsHelper;
import dev.obscuria.fragmentum.util.signal.Signal0;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class CollectionPanelDetails extends AbstractDetailsPanel<CachedElixir> {

    private final SelectionState<TabType> tabSelection;
    private final Signal0 styleChanged;

    protected CollectionPanelDetails(
            SelectionState<CachedElixir> selection, Signal0 styleChanged,
            int x, int y, int width, int height
    ) {
        super(selection, x, y, width, height);
        this.tabSelection = new SelectionState<>(TabType.INFO);
        this.tabSelection.listen(this, tab -> this.updateDetails());
        this.styleChanged = styleChanged;
        this.header.addChild(Util.make(new SplitContainer(0.5f, 0, false), it -> {
            it.addChild(new TabButton(tabSelection, TabType.INFO));
            it.addChild(new TabButton(tabSelection, TabType.STYLE));
            it.rect.setHeight(14);
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
            this.content.addChild(new ContentDetails(ContentsHelper.elixir(target.get())));
            if (!target.recipe().isEmpty()) this.content.addChild(new RecipeDetails(target.recipe()));
            this.content.addChild(new AssessmentDetails(target));
            this.content.addChild(new MasteryDetails(target));
            this.content.addChild(new ScoreDetails(target));
            this.content.addChild(new ActionDetails(target));
        } else {
            this.content.addChild(new ParagraphControl(ElixirumUI.STYLE_HINT, true));
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

        private final SelectionState<TabType> selection;
        private final TabType tabType;

        public TabButton(SelectionState<TabType> selection, TabType tabType) {
            super(tabType.displayName());
            this.clickAction = ClickAction.leftClick(this::onSelect);
            this.selection = selection;
            this.tabType = tabType;
        }

        public boolean isSelected() {
            return this.tabType == selection.get();
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            if (!transform.isWithinScissor()) return;
            this.isHovered = transform.isMouseOver(mouseX, mouseY);
            var font = Minecraft.getInstance().font;
            this.renderButton(graphics, transform, mouseX, mouseY);
            var color = isSelected() ? ArsElixirumPalette.WHITE
                    : isHovered ? ArsElixirumPalette.LIGHT
                    : ArsElixirumPalette.MODERATE;
            graphics.drawCenteredString(font, getButtonName(), (int) rect.centerX(), rect.y() + 3, color.decimal());
        }

        @Override
        public void renderButton(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            if (!isSelected()) return;
            RenderSystem.enableBlend();
            GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.SHINE_X64, this, -4, -4, 8, 8);
            RenderSystem.disableBlend();
        }

        private void onSelect(TabButton self) {
            if (isSelected()) return;
            this.selection.set(tabType);
        }
    }
}