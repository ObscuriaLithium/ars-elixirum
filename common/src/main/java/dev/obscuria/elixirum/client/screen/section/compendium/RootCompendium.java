package dev.obscuria.elixirum.client.screen.section.compendium;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.container.ListContainer;
import dev.obscuria.elixirum.client.screen.container.ScrollContainer;
import dev.obscuria.elixirum.client.screen.section.AbstractSection;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class RootCompendium extends AbstractSection {
    private static @Nullable ScrollContainer pageScroll;
    private static @Nullable ListContainer pageList;
    private static @Nullable ContentsType selectedPage;

    public RootCompendium(int center, Consumer<AbstractSection> action) {
        super(center, Type.COMPENDIUM, action);
    }

    @Override
    public void initTab(ElixirumScreen screen) {
        screen.addRenderableWidget(new PanelCompendium(screen.left(10), 10, screen.width(-20), screen.height(-20)));
        propagateUpdate();
    }

    static void bind(ScrollContainer scroll, ListContainer list) {
        pageScroll = scroll;
        pageList = list;
    }

    static boolean isSelected(ContentsType type) {
        return selectedPage == type;
    }

    static void select(ContentsType type) {
        if (isSelected(type)) return;
        selectedPage = type;
        propagateUpdate();
    }

    static void propagateUpdate() {
        if (selectedPage == null) return;
        if (pageScroll == null) return;
        if (pageList == null) return;
        pageList.children().clear();
        selectedPage.buildPage(pageList);
        pageScroll.resetScroll();
    }

    static {
        selectedPage = ContentsType.INTRODUCTION;
    }
}
