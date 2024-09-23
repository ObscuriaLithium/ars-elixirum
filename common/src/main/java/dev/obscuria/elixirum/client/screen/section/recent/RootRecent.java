package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.section.AbstractSection;
import dev.obscuria.elixirum.client.screen.widget.ElixirOverview;
import dev.obscuria.elixirum.client.screen.widget.ProgressDisplay;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Consumer;

public final class RootRecent extends AbstractSection {
    static @Nullable WeakReference<ClientAlchemy.RecentElixir> selected;
    static Consumer<ClientAlchemy.RecentElixir> updateListener = elixir -> {};

    public RootRecent(int center, Consumer<AbstractSection> action) {
        super(center, Type.RECENT, action);
    }

    @Override
    public void initTab(ElixirumScreen screen) {
        final var overview = screen.addRenderableWidget(new ElixirOverview(screen.left(screen.width(0) / 2), screen.height));
        screen.addRenderableWidget(new ProgressDisplay(screen.left(screen.width(0) / 2), 0));
        screen.addRenderableWidget(new PanelDetails(overview, screen.right(-130), 10, 120, screen.height(-20)));
        screen.addRenderableWidget(new PanelElixirs(screen.left(10), 10, 120, screen.height-20));
        propagateUpdate();
    }

    static Optional<ClientAlchemy.RecentElixir> getSelected() {
        return selected != null
                ? Optional.ofNullable(selected.get())
                : Optional.empty();
    }

    static void select(ClientAlchemy.RecentElixir elixir) {
        selected = new WeakReference<>(elixir);
        propagateUpdate();
    }

    static void propagateUpdate() {
        getSelected().ifPresent(updateListener);
    }
}
