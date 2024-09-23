package dev.obscuria.elixirum.client.screen.section.collection;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.container.ListContainer;
import dev.obscuria.elixirum.client.screen.container.PanelContainer;
import dev.obscuria.elixirum.client.screen.container.ScrollContainer;
import dev.obscuria.elixirum.client.screen.widget.Text;
import net.minecraft.network.chat.Component;

final class PanelCollection extends PanelContainer {

    public PanelCollection(int x, int y, int width, int height) {
        super(x, y, width, height);
        final var Header = this.setHeader(new ListContainer());
        Header.addChild(new Text()
                .setContent(Component.literal("Collection"))
                .setStyle(Elixirum.STYLE)
                .setCentered(true)
                .setScale(1.2f));
        final var scroll = this.setContent(new ScrollContainer());
        scroll.addChild(new SubElixirsGrid());
    }
}
