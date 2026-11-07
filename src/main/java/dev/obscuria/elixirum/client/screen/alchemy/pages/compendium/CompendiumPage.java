package dev.obscuria.elixirum.client.screen.alchemy.pages.compendium;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.screen.alchemy.guide.Compendium;
import dev.obscuria.elixirum.client.screen.alchemy.guide.contents.ContentBlock;
import dev.obscuria.elixirum.client.screen.alchemy.guide.contents.ImageBlock;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.toolkit.containers.*;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.HeaderControl;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class CompendiumPage extends PageContainer {

    private static final ImageBlock LOGO;
    private static Compendium.Page lastPage = Compendium.Page.EMPTY;

    private final ScrollContainer scroll;
    private final ListContainer contents;

    public CompendiumPage(
            Compendium compendium,
            Selection<Compendium.Page> selection,
            int x, int y, int width, int height
    ) {
        super(x, y, width, height);
        selection.addListener(this::openPage);
        this.scroll = setBody(new ScrollContainer(CommonComponents.EMPTY));
        this.contents = scroll.add(ListContainer.createBuilder()
                .padding(10)
                .separation(10)
                .build());
        selection.set(lastPage.isEmpty()
                ? compendium.findHomepage()
                : lastPage);
    }

    private void openPage(Compendium.Page page) {
        lastPage = page;
        this.scroll.resetScroll();
        this.contents.clearChildren();
        removeHeader();
        setVisible(!page.isEmpty());
        if (page.isEmpty()) return;

        setHeader(page.isHomepage()
                ? LOGO.instantiate()
                : new HeaderControl(Component.literal(page.title())));
        page.contents().stream()
                .map(ContentBlock::instantiate)
                .forEach(contents::addChild);
    }

    static {
        LOGO = new ImageBlock(ArsElixirum.identifier("textures/gui/logo.png"), 900, 208, 80);
    }
}
