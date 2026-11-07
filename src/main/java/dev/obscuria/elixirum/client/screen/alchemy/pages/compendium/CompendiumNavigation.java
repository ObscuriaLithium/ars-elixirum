package dev.obscuria.elixirum.client.screen.alchemy.pages.compendium;

import com.mojang.serialization.DataResult;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.guide.Compendium;
import dev.obscuria.elixirum.client.screen.toolkit.*;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PageContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.*;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.FootnoteControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.HeaderControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.ParagraphControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Label;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

final class CompendiumNavigation extends PageContainer {

    private final List<TopicButton> topics = new ArrayList<>();

    public CompendiumNavigation(
            Compendium compendium,
            Selection<Compendium.Page> selection,
            int x, int y, int width, int height
    ) {
        super(x, y, width, height);

        setHeader(Util.make(ListContainer.createBuilder().separation(4).build(), header -> {
            header.add(new HeaderControl(compendium.index().titleComponent()));
            header.add(new FootnoteControl(compendium.index().subtitleComponent()));
            header.add(SearchControl.withHandler(Component.literal("Search topics..."), this::filterTopics));
        }));

        setBody(Util.make(new ScrollContainer(Component.literal("No results")), body -> {
            var topicList = body.add(ListContainer.createBuilder().separation(1).build());
            addTopic(topicList, new TopicButton(compendium.findPage(compendium.index().homepage()), selection));

            for (var section : compendium.index().sections()) {
                var container = topicList.add(new SectionContainer(section.titleComponent()));
                for (var pageKey : section.pages()) {
                    addTopic(container, new TopicButton(compendium.findPage(pageKey), selection));
                }
            }
        }));
    }

    private void addTopic(Control parent, TopicButton topic) {
        this.topics.add(topic);
        parent.addChild(topic);
    }

    private void buildError(DataResult.PartialResult<Compendium> result) {
        this.setHeader(new HeaderControl(ElixirumUI.COMPENDIUM_HEADER));
        this.setBody(new ParagraphControl(Component.literal(result.message())));
    }

    private void filterTopics(String filter) {
        for (var topic : this.topics) {
            topic.filterTopic(filter);
        }
    }

    private static class SectionContainer extends Control {

        private static final int SEPARATION = 1;

        private Label label = Label.EMPTY;

        public SectionContainer(Component title) {
            super(0, 0, 0, 0, title);
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            label.draw(graphics, getX(), getY() + 10, Palette.MODERATE.decimal());
            graphics.hLine(getX(), getX() + getWidth() - 1,
                    getY() + 10 + label.getHeight() + 1,
                    Palette.DARKEST.decimal());
            renderChildren(graphics, context, mouseX, mouseY);
        }

        @Override
        protected void measure() {
            this.label = Label.create(getMessage(), getWidth(), GuiToolkit.PARAGRAPH_SCALE);
            int totalHeight = 10 + label.getHeight() + 8;
            boolean any = false;
            for (var child : getChildren()) {
                if (!child.visible) continue;
                totalHeight += child.getMeasuredHeight() + SEPARATION;
                any = true;
            }
            if (!any) totalHeight = 0;
            setMeasuredSize(0, totalHeight);
            setVisible(any);
        }

        @Override
        protected void layout() {
            int innerWidth = Math.max(0, getWidth());
            int offset = 10 + label.getHeight() + 8;
            for (var child : getChildren()) {
                if (!child.visible) continue;
                placeChild(child, getX(), getY() + offset, innerWidth, child.getMeasuredHeight());
                offset += child.getHeight() + SEPARATION;
            }
        }
    }

    private static class TopicButton extends ButtonControl {

        private final Selection<Compendium.Page> selection;
        private final Compendium.Page page;
        private final ItemStack icon;

        private Component displayName;
        private Label label = Label.EMPTY;

        public TopicButton(Compendium.Page page, Selection<Compendium.Page> selection) {
            super(CommonComponents.EMPTY);
            this.setSizeHints(SIZE_HINT_WIDTH);
            this.icon = page.icon().orElse(Items.PAPER).getDefaultInstance();
            this.displayName = Component.literal(page.title());
            this.selection = selection;
            this.page = page;
        }

        public void filterTopic(String filter) {
            this.displayName = GuiToolkit.highlight(page.title(), filter);
            setVisible(filter.isBlank() || StringUtils.containsIgnoreCase(page.title(), filter));
            markDirty();
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            if (selection.is(page)) GuiToolkit.drawBubbleParallax(graphics, context, this);
            if (isHovered) GuiToolkit.draw(graphics, context, Textures.SOLID_LIGHT, this, 0.1f);
            graphics.pose().pushPose();
            graphics.pose().translate(getX(), getCenterY(), 0);
            graphics.renderItem(icon, 1, -8);
            this.label.drawVCentered(graphics, 20, 0,
                    selection.is(page)
                            ? Palette.WHITE.decimal()
                            : Palette.LIGHT.decimal());
            graphics.pose().popPose();
        }

        @Override
        protected void measure() {
            this.label = Label.create(displayName, getWidth() - 22, 0.75f);
            setRequiredHeight(Math.max(18, 6 + label.getHeight()));
        }

        @Override
        protected void onClick() {
            this.selection.set(page);
        }

        @Override
        protected boolean hasOwnContent() {
            return true;
        }
    }
}
