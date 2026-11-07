package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.layout.HSpacingControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.SubheaderControl;
import net.minecraft.network.chat.Component;

public abstract class AbstractDetails extends ListContainer {

    protected AbstractDetails(Component title) {
        this.addChild(new HSpacingControl(12));
        this.addChild(new SubheaderControl(title));
        this.addChild(new HSpacingControl(4));
    }
}
