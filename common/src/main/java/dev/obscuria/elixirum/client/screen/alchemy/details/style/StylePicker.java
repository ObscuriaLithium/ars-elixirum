package dev.obscuria.elixirum.client.screen.alchemy.details.style;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.fragmentum.util.signal.Signal0;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class StylePicker<T> extends GridContainer {

    private final StyleAdapter<T> adapter;
    private final CachedElixir elixir;
    private final Signal0 changed;
    private T pointer;

    public StylePicker(StyleAdapter<T> adapter, CachedElixir elixir, Signal0 changed) {
        super(0);
        this.adapter = adapter;
        this.elixir = elixir;
        this.changed = changed;
        this.pointer = adapter.get(elixir);
        for (var value : adapter.values())
            this.addChild(new Slot(value));
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }

    private boolean isLocked(T value) {
        var level = ClientAlchemy.localProfile().mastery().getLevel();
        return adapter.requiredLevel(value) >= level;
    }

    private void apply(T value) {
        this.adapter.apply(elixir, value);
        this.pointer = value;
        this.changed.emit();
    }

    private class Slot extends Control {

        private final T value;
        private final Tooltip tooltip;

        private Slot(T value) {
            this.value = value;
            if (!isLocked(value)) {
                this.clickSound = adapter.sound(value);
                this.clickHandler = ClickHandler.leftClick(Slot.class, this::onSelectUnlocked);
                this.tooltip = Tooltip.create(adapter.displayName(value));
            } else {
                var requiredMastery = adapter.requiredLevel(value);
                this.tooltip = Tooltip.create(Component.empty()
                        .append(ElixirumUI.UNKNOWN)
                        .append(CommonComponents.NEW_LINE)
                        .append(Component
                                .translatable("style.elixirum.locked", requiredMastery)
                                .withStyle(ChatFormatting.GRAY)));
            }
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            var hovered = context.isMouseOver(this, mouseX, mouseY);
            if (pointer == value) GuiToolkit.draw(graphics, Textures.OUTLINE_WHITE, this);
            else if (hovered) GuiToolkit.draw(graphics, Textures.OUTLINE_PURPLE, this);

            if (isLocked(value)) {
                RenderSystem.enableBlend();
                GuiToolkit.draw(graphics, Textures.LOCK, getX() + 1, getY() + 1, 13, 13);
                RenderSystem.disableBlend();
            } else {
                adapter.render(graphics, context, getX(), getY(), elixir, value);
            }

            if (hovered) AlchemyScreen.tooltip = tooltip;
        }

        @Override
        protected void measure() {
            setMeasuredSize(15, 15);
        }

        private boolean onSelectUnlocked(Slot self) {
            StylePicker.this.apply(value);
            return true;
        }
    }
}