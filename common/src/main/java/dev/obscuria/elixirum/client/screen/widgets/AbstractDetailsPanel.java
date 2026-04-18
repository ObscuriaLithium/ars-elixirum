package dev.obscuria.elixirum.client.screen.widgets;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PanelContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HeaderControl;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class AbstractDetailsPanel extends PanelContainer {

    protected final SelectionState<CachedElixir> selection;
    protected final ScrollContainer scroll;
    protected final ListContainer header;
    protected final HeaderControl title;
    protected final ListContainer content;

    public AbstractDetailsPanel(SelectionState<CachedElixir> selection, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.selection = selection;
        this.selection.listen(this, this::updateDetails);
        this.header = setHeader(new ListContainer(0, 4, 0));
        this.title = header.addChild(new HeaderControl(Component.literal("Details")));

        this.visible = false;
        this.content = new ListContainer();
        this.scroll = new ScrollContainer(getPlaceholder());
        this.scroll.addChild(this.content);
        this.setBody(this.scroll);
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        super.render(graphics, transform, mouseX, mouseY);
    }

    protected abstract Component getPlaceholder();

    protected abstract void rebuild(CachedElixir target);

    protected Component getDisplayName() {
        return selection.get().displayName();
    }

    protected void updateDetails(CachedElixir target) {
        this.isChanged = true;
        this.visible = !target.isEmpty();
        this.title.setContent(getDisplayName());
        this.content.clearChildren();
        this.scroll.resetScroll();
        this.removeFooter();
        this.rebuild(target);
    }

    protected void updateDetails() {
        updateDetails(selection.get());
    }
}
