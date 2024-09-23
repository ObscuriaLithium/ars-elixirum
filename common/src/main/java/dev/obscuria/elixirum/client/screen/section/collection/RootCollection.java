package dev.obscuria.elixirum.client.screen.section.collection;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.section.AbstractSection;
import dev.obscuria.elixirum.client.screen.widget.ElixirOverview;
import dev.obscuria.elixirum.client.screen.widget.ProgressDisplay;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Consumer;

public final class RootCollection extends AbstractSection {
    static @Nullable WeakReference<Elixir> selected;
    static Consumer<Elixir> updateListener = elixir -> {};

    public RootCollection(int center, Consumer<AbstractSection> action) {
        super(center, Type.COLLECTION, action);
    }

    @Override
    public void initTab(ElixirumScreen screen) {
        final var overview = screen.addRenderableWidget(new ElixirOverview(screen.left(screen.width(0) / 2), screen.height));
        screen.addRenderableWidget(new ProgressDisplay(screen.left(screen.width(0) / 2), 0));
        screen.addRenderableWidget(new PanelDetails(overview, screen.right(-130), 10, 120, screen.height(-20)));
        screen.addRenderableWidget(new PanelCollection(screen.left(10), 10, 120, screen.height-20));
    }

    static HolderGetter<Essence> essenceGetter() {
        return Optional.ofNullable(Minecraft.getInstance().level)
                .map(level -> level.registryAccess().lookupOrThrow(ElixirumRegistries.ESSENCE))
                .orElseThrow();
    }

    static Optional<Elixir> getSelected() {
        return selected != null
                ? Optional.ofNullable(selected.get())
                : Optional.empty();
    }

    static void select(Elixir elixir) {
        selected = new WeakReference<>(elixir);
        propagateUpdate();
    }

    static void propagateUpdate() {
        getSelected().ifPresent(updateListener);
    }

    public record Elixir(ElixirHolder holder, ItemStack stack) {}
}
