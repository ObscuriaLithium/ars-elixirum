package dev.obscuria.elixirum.client.screen.widgets.pages.discoveries;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PanelContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HeaderControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.basics.EssenceHolder;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.Comparator;

class DiscoveriesPanelEffects extends PanelContainer {

    protected DiscoveriesPanelEffects(SelectionState<Essence> selection, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setHeader(new HeaderControl(ElixirumUI.DISCOVERIES_HEADER));
        this.setBody(Util.make(new ScrollContainer(CommonComponents.EMPTY), scroll -> {
            var list = new ListContainer(0, 1, 0);
            ClientAlchemy.INSTANCE.essences().streamHolders()
                    .filter(EssenceHolder::isBound)
                    .map(it -> new Entry(selection, it.get().orElseThrow()))
                    .sorted().forEach(list::addChild);
            scroll.addChild(list);
        }));
        this.setFooter(ParagraphControl.panelFooter(ElixirumUI.DISCOVERIES_FOOTER));
    }

    private static class Entry extends HierarchicalControl implements Comparable<Entry> {

        private final SelectionState<Essence> selection;
        private final Essence essence;
        private final boolean discovered;
        private MultiLineLabel title = MultiLineLabel.EMPTY;
        private Component summary = Component.empty();

        public Entry(SelectionState<Essence> selection, Essence essence) {
            super(0, 0, 0, 0, CommonComponents.EMPTY);
            this.selection = selection;
            this.essence = essence;
            this.clickAction = ClickAction.leftClick(it -> selection.set(essence));
            this.discovered = ClientAlchemy.localProfile().knownEffects().isKnown(essence.effect().value());
            this.setUpdateFlags(UPDATE_BY_WIDTH);
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            if (!transform.isWithinScissor()) return;

            if (transform.isMouseOver(mouseX, mouseY)) {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.1f);
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.SOLID_LIGHT, this);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
            }

            boolean isSmall = rect.height() <= 20;

            this.renderIcon(graphics, rect.x(), rect.y() + rect.height() / 2);

            graphics.pose().pushPose();
            graphics.pose().translate(rect.x() + 21, rect.y() + (isSmall ? 3 : 2), 0);
            graphics.pose().scale(0.75f, 0.75f, 0.75f);
            title.renderLeftAligned(graphics, 0, 0, 9, ArsElixirumPalette.LIGHT.decimal());
            graphics.pose().popPose();

            graphics.pose().pushPose();
            graphics.pose().translate(rect.x() + 21, rect.bottom() - (isSmall ? 9 : 8), 0);
            graphics.pose().scale(0.75f, 0.75f, 0.75f);
            var font = Minecraft.getInstance().font;
            graphics.drawString(font, summary, 0, 0, ArsElixirumPalette.MODERATE.decimal());
            graphics.pose().popPose();

            if (selection.is(essence)) {
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_WHITE, this);
            }
        }

        @Override
        public void reorganize() {
            var font = Minecraft.getInstance().font;
            this.title = MultiLineLabel.create(font, getDisplayName(), rect.width() - 10);
            this.summary = getCategoryName(essence.effect().value().getCategory());
            this.rect.setHeight(Math.max(20, 2 + (int) Math.ceil(7.5f * title.getLineCount() - 1) + 8));
        }

        @Override
        public int compareTo(DiscoveriesPanelEffects.Entry other) {
            return Comparator
                    .comparing(Entry::isDiscovered, Comparator.reverseOrder())
                    .thenComparing(Entry::sortKey)
                    .compare(this, other);
        }

        @Override
        protected boolean hasContents() {
            return true;
        }

        private void renderIcon(GuiGraphics graphics, int x, int y) {
            if (!discovered) GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.BLACK);
            var textures = Minecraft.getInstance().getMobEffectTextures();
            var texture = textures.get(essence.effect().value());
            graphics.blit(x, y - 9, 0, 18, 18, texture);
            if (!discovered) GuiGraphicsUtil.resetShaderColor();
        }

        private boolean isDiscovered() {
            return discovered;
        }

        private String sortKey() {
            return essence.displayName().getString();
        }

        private Component getDisplayName() {
            return discovered ? essence.displayName() : ElixirumUI.EFFECT_UNKNOWN;
        }

        private Component getCategoryName(MobEffectCategory category) {
            return switch (category) {
                case HARMFUL -> ElixirumUI.EFFECT_HARMFUL;
                case BENEFICIAL -> ElixirumUI.EFFECT_BENEFICIAL;
                default -> ElixirumUI.EFFECT_NEUTRAL;
            };
        }
    }
}