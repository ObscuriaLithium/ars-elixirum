package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.tool.Accessor;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.container.ListContainer;
import dev.obscuria.elixirum.client.screen.container.PanelContainer;
import dev.obscuria.elixirum.client.screen.container.ScrollContainer;
import dev.obscuria.elixirum.client.screen.container.SpoilerContainer;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDetailsPanel extends PanelContainer {
    private static boolean capSpoilerCache = true;
    private static boolean shapeSpoilerCache = true;
    protected final List<UpdateListener> listeners = Lists.newArrayList();

    public AbstractDetailsPanel(ElixirOverview overview, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setHeader(createHeader());
        this.createFooter().ifPresent(this::setFooter);
        final var scroll = this.setContent(new ScrollContainer());
        final var content = scroll.addChild(new ListContainer().setSeparation(2));
        this.buildContents(content);
        this.listeners.add((stack, recipe) -> {
            overview.setStack(stack);
            scroll.resetScroll();
        });
    }

    public void update(ItemStack stack, ElixirRecipe recipe) {
        this.listeners.forEach(listener -> listener.update(stack, recipe));
    }

    protected void buildContents(ListContainer container) {
        container.addChild(createEffects());
        container.addChild(createRecipe());
        container.addChild(createCapPicker());
        container.addChild(createShapePicker());
    }

    protected HierarchicalWidget createHeader() {
        final var widget = new Text()
                .setContent(Component.literal("Details"))
                .setStyle(Elixirum.STYLE)
                .setCentered(true)
                .setScale(1.2f);
        this.listeners.add(((stack, recipe) -> widget.setContent(stack.getHoverName())));
        return widget;
    }

    protected Optional<HierarchicalWidget> createFooter() {
        return Optional.empty();
    }

    protected HierarchicalWidget createEffects() {
        final var widget = new ListContainer();
        this.listeners.add(((stack, recipe) -> {
            widget.children().clear();
            for (var effect : ElixirContents.get(stack).effects())
                widget.addChild(new EffectDisplay(effect));
        }));
        return widget;
    }

    protected HierarchicalWidget createRecipe() {
        final var widget = new ListContainer();
        this.listeners.add((stack, recipe) -> {
            widget.children().clear();
            widget.addChild(new RecipeDisplay(recipe));
        });
        return widget;
    }

    protected HierarchicalWidget createCapPicker() {
        final var spoiler = new SpoilerContainer(Component.literal("Cap")).setAccessor(
                Accessor.create(() -> capSpoilerCache, value -> capSpoilerCache = value));
        final var picker = spoiler.addChild(new StylePicker.Cap());
        this.listeners.add(((stack, recipe) -> picker.setStack(stack)));
        return spoiler;
    }

    protected HierarchicalWidget createShapePicker() {
        final var spoiler = new SpoilerContainer(Component.literal("Shape")).setAccessor(
                Accessor.create(() -> shapeSpoilerCache, value -> shapeSpoilerCache = value));
        final var picker = spoiler.addChild(new StylePicker.Shape());
        this.listeners.add(((stack, recipe) -> picker.setStack(stack)));
        return spoiler;
    }

    @FunctionalInterface
    protected interface UpdateListener {

        void update(ItemStack stack, ElixirRecipe recipe);
    }
}
