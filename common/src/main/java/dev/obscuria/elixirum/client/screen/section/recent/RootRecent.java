package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.section.AbstractSection;
import dev.obscuria.elixirum.client.screen.widget.ElixirOverview;
import dev.obscuria.elixirum.client.screen.widget.ProgressDisplay;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public final class RootRecent extends AbstractSection {
    private static @Nullable ElixirHolder selectedHolder;
    private static Consumer<Optional<ElixirHolder>> selectionListener = elixir -> {};

    public RootRecent(int center, Consumer<AbstractSection> action) {
        super(center, Type.RECENT, action);
    }

    @Override
    public void initSection(ElixirumScreen screen) {
        final var overview = screen.addRenderableWidget(new ElixirOverview(screen.left(screen.width(0) / 2), screen.height));
        screen.addRenderableWidget(new ProgressDisplay(screen.left(screen.width(0) / 2), 0));
        screen.addRenderableWidget(new PanelDetails(overview, screen.right(-130), 10, 120, screen.height(-20)));
        screen.addRenderableWidget(new PanelElixirs(screen.left(10), 10, 120, screen.height-20));
        propagateUpdate();
    }

    @Override
    public void updateSection() {

    }

    public static void reset() {
        selectedHolder = null;
        selectionListener = elixir -> {};
    }

    static void setSelectionListener(Consumer<Optional<ElixirHolder>> consumer) {
        selectionListener = consumer;
    }

    static void select(@Nullable ElixirHolder holder) {
        selectedHolder = holder;
        propagateUpdate();
    }

    static Optional<ElixirHolder> getSelectedHolder() {
        return Optional.ofNullable(selectedHolder);
    }

    static void propagateUpdate() {
        selectionListener.accept(getSelectedHolder());
    }
}
