package dev.obscuria.elixirum.client.screen.section.compendium;

import dev.obscuria.elixirum.client.screen.ElixirumPalette;
import dev.obscuria.elixirum.client.screen.widget.Text;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

final class SubText extends Text {

    public SubText(Component content) {
        this.setContent(content);
        this.setStyle(Style.EMPTY.withColor(ElixirumPalette.LIGHT));
    }
}
