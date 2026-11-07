package dev.obscuria.elixirum.client.screen.alchemy.containers;

import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.PageContainer;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ScrollContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.HeaderControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import net.minecraft.network.chat.Component;

public abstract class AbstractDetailsPage<T> extends PageContainer {

    protected final Selection<T> selection;
    protected final ScrollContainer scroll;
    protected final ListContainer header;
    protected final HeaderControl title;
    protected final ListContainer content;

    public AbstractDetailsPage(Selection<T> selection, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.selection = selection;
        this.selection.addListener(this::updateDetails);
        this.header = setHeader(ListContainer.createBuilder().separation(4).build());
        this.title = header.add(new HeaderControl(Component.literal("Details")));

        this.visible = false;
        this.content = new ListContainer();
        this.scroll = new ScrollContainer(getPlaceholder());
        this.scroll.addChild(this.content);
        this.setBody(this.scroll);
    }

    protected abstract Component getPlaceholder();

    protected abstract void rebuild(T target);

    protected abstract Component getDisplayName(T target);

    protected abstract boolean isEmpty(T target);

    protected void updateDetails(T target) {
        this.visible = !isEmpty(target);
        this.title.setContent(getDisplayName(target));
        this.content.clearChildren();
        this.scroll.resetScroll();
        this.removeFooter();
        this.rebuild(target);
        this.markDirty();
    }

    protected void updateDetails() {
        updateDetails(selection.get());
    }
}
