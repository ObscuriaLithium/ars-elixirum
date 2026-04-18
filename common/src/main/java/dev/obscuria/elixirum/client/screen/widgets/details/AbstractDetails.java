package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.SpacingControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.SubHeaderControl;
import net.minecraft.network.chat.Component;

public abstract class AbstractDetails extends ListContainer {

    protected AbstractDetails(Component title) {
        this.addChild(new SpacingControl(12));
        this.addChild(new SubHeaderControl(title));
        this.addChild(new SpacingControl(4));
    }
}
