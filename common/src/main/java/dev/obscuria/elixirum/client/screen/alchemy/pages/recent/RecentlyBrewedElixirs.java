package dev.obscuria.elixirum.client.screen.alchemy.pages.recent;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PageContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.FootnoteControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.HeaderControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

class RecentlyBrewedElixirs extends PageContainer {

    private static final Component HEADER_TEXT;
    private static final Component BODY_TEXT;
    private static final Component FOOTER_TEXT;

    protected RecentlyBrewedElixirs(Selection<CachedElixir> selection, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setHeader(new HeaderControl(HEADER_TEXT));
        this.setBody(Util.make(new ScrollContainer(BODY_TEXT), scroll -> {
            var list = ListContainer.createBuilder().separation(1).build();
            var container = new GridContainer(1);
            for (var recent : ClientAlchemy.INSTANCE.recentlyBrewed) {
                var widget = new RecentElixirControl(selection, recent);
                widget.makeClickHandler(selection::set);
                container.addChild(widget);
            }
            list.addChild(container);
            scroll.addChild(list);
        }));
        this.setFooter(new FootnoteControl(FOOTER_TEXT));
    }

    static {
        HEADER_TEXT = Component.translatable("ui.elixirum.recently_brewed.header");
        BODY_TEXT = Component.translatable("ui.elixirum.recently_brewed.body",
                Component.translatable("block.elixirum.glass_cauldron").withStyle(ChatFormatting.GOLD),
                Component.translatable("ui.elixirum.compendium.header").withStyle(ChatFormatting.GOLD));
        FOOTER_TEXT = Component.translatable("ui.elixirum.recently_brewed.footer");
    }
}