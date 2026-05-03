package dev.obscuria.elixirum.client.screen.alchemy.pages.compendium;

import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyPage;
import dev.obscuria.elixirum.client.screen.alchemy.guide.Compendium;
import dev.obscuria.elixirum.client.screen.alchemy.guide.CompendiumLoader;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;

public class CompendiumScreen extends AlchemyScreen {

    public CompendiumScreen() {
        super(AlchemyPage.COMPENDIUM);
    }

    @Override
    protected void init() {
        super.init();
        var compendiumResult = CompendiumLoader.load();
        compendiumResult.result().ifPresent(compendium -> {
            var selection = new Selection<>(Compendium.Page.EMPTY);
            addChild(new CompendiumNavigation(compendium, selection, left(10), top(10), 126, height(-20)));
            addChild(new CompendiumPage(compendium, selection, left(146), top(10), width(-156), height(-20)));
        });
    }
}
