package dev.obscuria.elixirum.client.screen.widgets.pages.recent;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PanelContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HeaderControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

class RecentPanelRecipes extends PanelContainer {

    private static final Component TITLE;
    private static final Component PLACEHOLDER;

    private final HeaderControl header;
    private final ScrollContainer scroll;

    protected RecentPanelRecipes(SelectionState<CachedElixir> selection, int x, int y, int width, int height) {
        super(x, y, width, height);

        this.header = setHeader(new HeaderControl(TITLE));
        this.scroll = setBody(Util.make(new ScrollContainer(PLACEHOLDER), scroll -> {
            final var list = new ListContainer(0, 1, 0);
            final var container = new GridContainer(1);
            for (var recent : ClientAlchemy.INSTANCE.recentlyBrewed) {
                final var widget = new RecentElixirWidget(selection, recent);
                widget.makeStackClickAction(selection::set);
                container.addChild(widget);
            }
            list.addChild(container);
            scroll.addChild(list);
        }));
        this.setFooter(ParagraphControl.panelFooter(Component.literal("All unsaved recipes will be lost upon leaving the world!")));
    }

    static {
        TITLE = Component.translatable("elixirum.screen.recent.recipes.title");
        PLACEHOLDER = Component.translatable("elixirum.screen.recent.recipes.placeholder",
                Component.translatable("block.elixirum.glass_cauldron").withStyle(ChatFormatting.GOLD),
                Component.translatable("elixirum.screen.compendium.title").withStyle(ChatFormatting.GOLD));
    }
}