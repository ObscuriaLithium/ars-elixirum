package dev.obscuria.elixirum.client.screen.alchemy.pages.discoveries;

import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.FootnoteControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Label;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PageContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.HeaderControl;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import dev.obscuria.elixirum.common.alchemy.registry.EssenceHolder;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.Comparator;

final class DiscoveriesEffects extends PageContainer {

    DiscoveriesEffects(Selection<DiscoveryTarget> selection, int x, int y, int width, int height) {
        super(x, y, width, height);
        setHeader(new HeaderControl(ElixirumUI.DISCOVERIES_HEADER));
        setBody(Util.make(new ScrollContainer(CommonComponents.EMPTY), scroll -> {
            var entryList = scroll.add(ListContainer.createBuilder().separation(1).build());
            ClientAlchemy.INSTANCE.essences().streamHolders()
                    .filter(EssenceHolder::isBound)
                    .map(it -> {
                        var essence = it.get().orElseThrow();
                        var target = new DiscoveryTarget(essence);
                        return new Entry(selection, target, essence);
                    }).sorted().forEach(entryList::addChild);
        }));
        setFooter(new FootnoteControl(ElixirumUI.DISCOVERIES_FOOTER));
    }

    private static class Entry extends ButtonControl implements Comparable<Entry> {

        private final Selection<DiscoveryTarget> selection;
        private final DiscoveryTarget target;
        private final Essence essence;
        private final boolean discovered;

        private Label label = Label.EMPTY;

        public Entry(Selection<DiscoveryTarget> selection, DiscoveryTarget target, Essence essence) {
            super(CommonComponents.EMPTY);
            this.discovered = ClientAlchemy.localProfile().knownEffects().isKnown(essence.effect().value());
            this.selection = selection;
            this.target = target;
            this.essence = essence;
            this.setSizeHints(SIZE_HINT_WIDTH);
        }

        @Override
        public int compareTo(Entry other) {
            return Comparator
                    .comparing(Entry::isDiscovered, Comparator.reverseOrder())
                    .thenComparing(Entry::sortKey)
                    .compare(this, other);
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            if (!context.isVisible(this)) return;
            if (selection.is(target)) GuiToolkit.drawBubbleParallax(graphics, context, this);
            if (isHovered) GuiToolkit.draw(graphics, context, Textures.SOLID_LIGHT, this, 0.1f);
            graphics.pose().pushPose();
            graphics.pose().translate(getX(), getCenterY(), 0);
            renderIcon(graphics, context, 1, 0);
            this.label.drawVCentered(graphics, 21, 0,
                    selection.is(target)
                            ? Palette.WHITE.decimal()
                            : Palette.LIGHT.decimal());
            graphics.pose().popPose();
        }

        @Override
        protected void measure() {
            var categoryName = getCategoryName(essence.effect().value().getCategory());
            this.label = Label.create(Component.empty()
                            .append(getDisplayName())
                            .append(CommonComponents.NEW_LINE)
                            .append(GuiToolkit.dye(categoryName, Palette.MODERATE)),
                    getWidth() - 20, GuiToolkit.PARAGRAPH_SCALE);
            setRequiredHeight(Math.max(20, label.getHeight()));
        }

        @Override
        protected boolean hasOwnContent() {
            return true;
        }

        @Override
        protected void onClick() {
            this.selection.set(target);
        }

        private void renderIcon(GuiGraphics graphics, GuiContext context, int x, int y) {
            if (!discovered) context.pushModulate(Palette.BLACK);
            var textures = Minecraft.getInstance().getMobEffectTextures();
            var texture = textures.get(essence.effect().value());
            graphics.blit(x, y - 9, 0, 18, 18, texture);
            if (!discovered) context.popModulate();
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