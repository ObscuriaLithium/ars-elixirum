package dev.obscuria.elixirum.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.container.GridContainer;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirCap;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirShape;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.registry.ElixirumDataComponents;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import dev.obscuria.elixirum.util.TextWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public abstract class StylePicker<T> extends GridContainer {
    private static final ResourceLocation LOCKED = Elixirum.key("icon/locked");
    private @Nullable ItemStack stack;
    private @Nullable T selected;

    public StylePicker() {
        this.values().forEach(value -> this.addChild(new Element(value))
                .setClickSound(ElixirumSounds.UI_CLICK_2.holder())
                .setClickAction(ClickAction.<Element>left(slot -> {
                    if (stack == null) return false;
                    if (this.isLocked(slot.value)) return false;
                    this.selected = slot.value;
                    this.onSelect(stack, slot.value);
                    return true;
                })));
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        this.selected = this.getInitialValue(stack);
    }

    protected abstract Stream<T> values();

    protected abstract void onSelect(ItemStack stack, T value);

    protected abstract T getInitialValue(ItemStack stack);

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

    public static final class Cap extends StylePicker<ElixirCap> {

        @Override
        protected Stream<ElixirCap> values() {
            return Stream.of(ElixirCap.values());
        }

        @Override
        protected void onSelect(ItemStack stack, ElixirCap value) {
            stack.set(
                    ElixirumDataComponents.ELIXIR_STYLE.value(),
                    ElixirStyle.get(stack).withCap(value));
        }

        @Override
        protected ElixirCap getInitialValue(ItemStack stack) {
            return ElixirStyle.getCap(stack);
        }

        @Override
        protected void renderElement(GuiGraphics graphics, int x, int y, ElixirCap value) {
            final var texture = Elixirum.key("textures/item/elixir/" + value.getTexture() + ".png");
            graphics.pose().pushPose();
            graphics.pose().translate(x + 6.75f, y + 7.5f, 0f);
            graphics.pose().scale(1.5f, 1.5f, 1.5f);
            graphics.blit(texture, -8, -3, 0, 0, 16, 16, 16, 16);
            graphics.pose().popPose();
        }

        @Override
        protected boolean isLocked(ElixirCap value) {
            return value.isLocked(
                    ClientAlchemy.getProfile().getTotalDiscoveredEssences(),
                    ClientAlchemy.getIngredients().getTotalEssences());
        }

        @Override
        protected Component getElementName(ElixirCap value) {
            return value.getDisplayName();
        }

        @Override
        protected List<? extends Component> getLockedTooltip(ElixirCap value) {
            final var required = value.getRequiredProgress(ClientAlchemy.getIngredients().getTotalEssences());
            final var discovered = ClientAlchemy.getProfile().getTotalDiscoveredEssences();
            final var wanting = required - discovered;
            return TextWrapper.create(Component.translatable("elixirum.style.locked", wanting))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                    .setMaxLength(24)
                    .build();
        }
    }

    public static final class Shape extends StylePicker<ElixirShape> {

        @Override
        protected Stream<ElixirShape> values() {
            return Stream.of(ElixirShape.values());
        }

        @Override
        protected void onSelect(ItemStack stack, ElixirShape value) {
            stack.set(
                    ElixirumDataComponents.ELIXIR_STYLE.value(),
                    ElixirStyle.get(stack).withShape(value));
        }

        @Override
        protected ElixirShape getInitialValue(ItemStack stack) {
            return ElixirStyle.getShape(stack);
        }

        @Override
        protected void renderElement(GuiGraphics graphics, int x, int y, ElixirShape value) {
            final var texture = Elixirum.key("textures/item/elixir/" + value.getTexture() + ".png");
            graphics.blit(texture, x - 1, y - 3, 0, 0, 16, 16, 16, 16);
        }

        @Override
        protected boolean isLocked(ElixirShape value) {
            return value.isLocked(
                    ClientAlchemy.getProfile().getTotalDiscoveredEssences(),
                    ClientAlchemy.getIngredients().getTotalEssences());
        }

        @Override
        protected Component getElementName(ElixirShape value) {
            return value.getDisplayName();
        }

        @Override
        protected List<? extends Component> getLockedTooltip(ElixirShape value) {
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
