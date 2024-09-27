package dev.obscuria.elixirum.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.container.GridContainer;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.tool.Property;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.common.alchemy.style.Cap;
import dev.obscuria.elixirum.common.alchemy.style.Shape;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import dev.obscuria.elixirum.util.TextWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public abstract class StylePicker<T> extends GridContainer {
    private static final ResourceLocation LOCKED = Elixirum.key("icon/locked");
    private @Nullable ElixirHolder holder;
    private @Nullable T selected;

    public StylePicker() {
        this.values().forEach(value -> this.addChild(new Element(value))
                .setClickSound(ElixirumSounds.UI_CLICK_2)
                .setClickAction(ClickAction.<Element>left(slot -> {
                    if (holder == null) return false;
                    if (this.isLocked(slot.value)) return false;
                    this.selected = slot.value;
                    this.onSelect(holder, slot.value);
                    return true;
                })));
    }

    public void bound(ElixirHolder holder) {
        this.holder = holder;
        this.selected = this.getFromStyle(holder.getStyle().orElse(ElixirStyle.DEFAULT));
    }

    protected abstract Stream<T> values();

    protected abstract void onSelect(ElixirHolder holder, T value);

    protected abstract T getFromStyle(ElixirStyle style);

    protected abstract void renderElement(GuiGraphics graphics, int x, int y, T value);

    protected abstract boolean isLocked(T value);

    protected abstract Component getElementName(T value);

    protected abstract List<? extends Component> getLockedTooltip(T value);

    protected void acceptTooltip(T value) {
        ElixirumScreen.tooltipProvider = () -> {
            var tooltip = Lists.<Component>newArrayList();
            tooltip.add(getElementName(value));
            if (isLocked(value))
                tooltip.addAll(getLockedTooltip(value));
            return tooltip;
        };
    }

    public static final class CapPicker extends StylePicker<Cap> {
        private static boolean cache = false;

        public static Property<Boolean> getProperty() {
            return Property.create(() -> cache, value -> cache = value);
        }

        @Override
        protected Stream<Cap> values() {
            return Stream.of(Cap.values());
        }

        @Override
        protected void onSelect(ElixirHolder holder, Cap value) {
            holder.setStyle(holder.getStyle().orElse(ElixirStyle.DEFAULT).withCap(value));
        }

        @Override
        protected Cap getFromStyle(ElixirStyle style) {
            return style.cap();
        }

        @Override
        protected void renderElement(GuiGraphics graphics, int x, int y, Cap value) {
            final var texture = Elixirum.key("textures/item/elixir/" + value.getTexture() + ".png");
            graphics.pose().pushPose();
            graphics.pose().translate(x + 6.75f, y + 7.5f, 0f);
            graphics.pose().scale(1.5f, 1.5f, 1.5f);
            graphics.blit(texture, -8, -3, 0, 0, 16, 16, 16, 16);
            graphics.pose().popPose();
        }

        @Override
        protected boolean isLocked(Cap value) {
            return value.isLocked(
                    ClientAlchemy.getProfile().getTotalDiscoveredEssences(),
                    ClientAlchemy.getIngredients().getTotalEssences());
        }

        @Override
        protected Component getElementName(Cap value) {
            return value.getDisplayName();
        }

        @Override
        protected List<? extends Component> getLockedTooltip(Cap value) {
            final var required = value.getRequiredProgress(ClientAlchemy.getIngredients().getTotalEssences());
            final var discovered = ClientAlchemy.getProfile().getTotalDiscoveredEssences();
            final var wanting = required - discovered;
            return TextWrapper.create(Component.translatable("elixirum.style.locked", wanting))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                    .setMaxLength(24)
                    .build();
        }
    }

    public static final class ShapePicker extends StylePicker<Shape> {
        private static boolean cache = false;

        public static Property<Boolean> getProperty() {
            return Property.create(() -> cache, value -> cache = value);
        }

        @Override
        protected Stream<Shape> values() {
            return Stream.of(Shape.values());
        }

        @Override
        protected void onSelect(ElixirHolder holder, Shape value) {
            holder.setStyle(holder.getStyle().orElse(ElixirStyle.DEFAULT).withShape(value));
        }

        @Override
        protected Shape getFromStyle(ElixirStyle style) {
            return style.shape();
        }

        @Override
        protected void renderElement(GuiGraphics graphics, int x, int y, Shape value) {
            final var texture = Elixirum.key("textures/item/elixir/" + value.getTexture() + ".png");
            graphics.blit(texture, x - 1, y - 3, 0, 0, 16, 16, 16, 16);
        }

        @Override
        protected boolean isLocked(Shape value) {
            return value.isLocked(
                    ClientAlchemy.getProfile().getTotalDiscoveredEssences(),
                    ClientAlchemy.getIngredients().getTotalEssences());
        }

        @Override
        protected Component getElementName(Shape value) {
            return value.getDisplayName();
        }

        @Override
        protected List<? extends Component> getLockedTooltip(Shape value) {
            final var required = value.getRequiredProgress(ClientAlchemy.getIngredients().getTotalEssences());
            final var discovered = ClientAlchemy.getProfile().getTotalDiscoveredEssences();
            final var wanting = required - discovered;
            return TextWrapper.create(Component.translatable("elixirum.style.locked", wanting))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                    .setMaxLength(24)
                    .build();
        }
    }

    private class Element extends HierarchicalWidget {
        private final T value;

        protected Element(T value) {
            super(0, 0, 15, 15, Component.empty());
            this.value = value;
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            ElixirumScreen.debugRenderer(this, graphics, transform, mouseX, mouseY);
            if (StylePicker.this.selected == value) {
                graphics.blitSprite(ElixirumScreen.SPRITE_OUTLINE_WHITE, getX(), getY(), getWidth(), getHeight());
            } else if (transform.isMouseOver(mouseX, mouseY)) {
                graphics.blitSprite(ElixirumScreen.SPRITE_OUTLINE_PURPLE, getX(), getY(), getWidth(), getHeight());
            }
            StylePicker.this.renderElement(graphics, getX(), getY(), value);
            if (StylePicker.this.isLocked(value)) {
                RenderSystem.enableBlend();
                graphics.blitSprite(LOCKED, getX() + 1, getY() + 1, 13, 13);
                RenderSystem.disableBlend();
            }
            if (transform.isMouseOver(mouseX, mouseY))
                StylePicker.this.acceptTooltip(value);
        }

        @Override
        protected void reorganize() {}
    }
}
