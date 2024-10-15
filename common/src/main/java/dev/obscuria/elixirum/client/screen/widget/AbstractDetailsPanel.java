package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.container.ListContainer;
import dev.obscuria.elixirum.client.screen.container.PanelContainer;
import dev.obscuria.elixirum.client.screen.container.ScrollContainer;
import dev.obscuria.elixirum.client.screen.container.SpoilerContainer;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirContents;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDetailsPanel extends PanelContainer
{
    protected final List<UpdateListener> listeners = Lists.newArrayList();

    public AbstractDetailsPanel(ElixirOverview overview, int x, int y, int width, int height)
    {
        super(x, y, width, height);
        this.setHeader(createHeader());
        this.createFooter().ifPresent(this::setFooter);
        final var scroll = this.setContent(new ScrollContainer(Component.literal("Nothing selected")));
        final var content = scroll.addChild(new ListContainer().setSeparation(2));
        this.buildContents(content);
        this.listeners.add(holder -> {
            final var stack = holder.getCachedStack().orElse(ItemStack.EMPTY);
            overview.setStack(stack);
            scroll.resetScroll();
            content.setVisible(!stack.isEmpty());
        });
        content.setVisible(false);
    }

    public void update(ElixirHolder holder)
    {
        this.listeners.forEach(listener -> listener.update(holder));
    }

    protected void buildContents(ListContainer container)
    {

        final var capSpoiler = new SpoilerContainer(Component.literal("Cap"))
                .setProperty(StylePicker.CapPicker.getProperty());
        final var capPicker = capSpoiler.addChild(createCapPicker());
        this.listeners.add((capPicker::bound));

        final var shapeSpoiler = new SpoilerContainer(Component.literal("Shape"))
                .setProperty(StylePicker.ShapePicker.getProperty());
        final var shapePicker = shapeSpoiler.addChild(createShapePicker());
        this.listeners.add((shapePicker::bound));

        container.addChild(createEffects());
        container.addChild(createRecipe());
        container.addChild(capSpoiler);
        container.addChild(shapeSpoiler);
    }

    protected HierarchicalWidget createHeader()
    {
        final var widget = new Text()
                .setContent(Component.literal("Details"))
                .setStyle(Elixirum.STYLE)
                .setCentered(true)
                .setScale(1.2f);
        this.listeners.add((holder -> {
            final var stack = holder.getCachedStack().orElse(ItemStack.EMPTY);
            widget.setContent(stack.isEmpty()
                    ? Component.literal("Details")
                    : stack.getHoverName());
        }));
        return widget;
    }

    protected Optional<HierarchicalWidget> createFooter()
    {
        return Optional.empty();
    }

    protected HierarchicalWidget createEffects()
    {
        final var widget = new ListContainer();
        this.listeners.add((holder -> {
            final var contents = holder.getCachedStack()
                    .map(ElixirContents::get)
                    .orElse(ElixirContents.WATER);
            widget.children().clear();
            for (var effect : contents.effects())
                widget.addChild(new EffectDisplay(effect));
        }));
        return widget;
    }

    protected HierarchicalWidget createRecipe()
    {
        final var widget = new ListContainer();
        this.listeners.add(holder -> {
            widget.children().clear();
            widget.addChild(new RecipeDisplay(holder.getRecipe()));
        });
        return widget;
    }

    protected StylePicker.CapPicker createCapPicker()
    {
        return new StylePicker.CapPicker();
    }

    protected StylePicker.ShapePicker createShapePicker()
    {
        return new StylePicker.ShapePicker();
    }

    @FunctionalInterface
    protected interface UpdateListener
    {
        void update(ElixirHolder holder);
    }
}
