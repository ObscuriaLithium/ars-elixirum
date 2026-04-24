package dev.obscuria.elixirum.client.screen.widgets.style;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
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
    public boolean hasContents() {
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

    private class Slot extends HierarchicalControl {

        private final T value;
        private final Tooltip tooltip;

        private Slot(T value) {
            super(0, 0, 15, 15, Component.empty());
            this.value = value;
            if (!isLocked(value)) {
                this.clickSound = adapter.sound(value);
                this.clickAction = ClickAction.leftClick(this::onSelectUnlocked);
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
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            var hovered = transform.isMouseOver(mouseX, mouseY);
            if (pointer == value) GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_WHITE, this);
            else if (hovered) GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_PURPLE, this);

            if (isLocked(value)) {
                RenderSystem.enableBlend();
                GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.LOCK, rect.x() + 1, rect.y() + 1, 13, 13);
                RenderSystem.disableBlend();
            } else {
                adapter.render(graphics, rect.x(), rect.y(), elixir, value);
            }

            if (hovered) AbstractPage.tooltip = tooltip;
        }

        @Override
        public void reorganize() {}

        private boolean onSelectUnlocked(Slot self) {
            StylePicker.this.apply(value);
            return true;
        }
    }
}