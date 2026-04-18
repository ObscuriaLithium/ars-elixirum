package dev.obscuria.elixirum.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.containers.GridContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.common.alchemy.style.Cap;
import dev.obscuria.elixirum.common.alchemy.style.Chroma;
import dev.obscuria.elixirum.common.alchemy.style.Shape;
import dev.obscuria.elixirum.common.registry.ElixirumSounds;
import dev.obscuria.fragmentum.util.signal.Signal0;
import dev.obscuria.fragmentum.util.signal.Signal1;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public abstract class StylePicker<T> extends GridContainer {

    public final Signal1<T> selected = new Signal1<>();
    protected final CachedElixir elixir;
    protected final Signal0 updated;
    protected T pointer;

    public StylePicker(Signal0 updated, CachedElixir elixir) {
        super(0);
        this.elixir = elixir;
        this.updated = updated;
    }

    @Override
    public boolean hasContents() {
        return true;
    }

    public HierarchicalControl createSlot(SoundEvent sound, T value) {
        return new Slot(sound, value);
    }

    protected abstract T get();

    protected abstract void render(GuiGraphics graphics, int x, int y, T value);

    protected abstract boolean isLocked(T value);

    protected abstract Component nameOf(T value);

    protected abstract Component descriptionOf(T value);

    public static class OfCap extends StylePicker<Cap> {

        public OfCap(Signal0 updated, CachedElixir cache) {
            super(updated, cache);

            for (Cap cap : Cap.values()) {
                this.addChild(createSlot(ElixirumSounds.UI_BOTTLE_OPEN, cap));
            }

            this.pointer = cache.configured().getStyle().cap();
            selected.connect(cap -> {
                cache.mapStyleSynced(style -> style.withCap(cap));
                this.updated.emit();
            });
        }

        @Override
        protected Cap get() {
            return elixir.configured().getStyle().cap();
        }

        @Override
        protected void render(GuiGraphics graphics, int x, int y, Cap value) {
            var texture = ArsElixirum.identifier("textures/item/elixir/" + value.texture + ".png");

            graphics.pose().pushPose();
            graphics.pose().translate(x + 6.75f, y + 7.5f, 0f);
            graphics.pose().scale(1.5f, 1.5f, 1.5f);
            graphics.blit(texture, -8, -3, 0f, 0f, 16, 16, 16, 16);
            graphics.pose().popPose();
        }

        @Override
        protected boolean isLocked(Cap value) {
            return value.isLocked(ClientAlchemy.INSTANCE.localProfile());
        }

        @Override
        protected Component nameOf(Cap value) {
            return value.displayName();
        }

        @Override
        protected Component descriptionOf(Cap value) {
            if (!isLocked(value)) return null;

            return Component
                    .translatable("style.elixirum.locked", value.mastery)
                    .withStyle(ChatFormatting.GRAY);
        }
    }

    public static class OfShape extends StylePicker<Shape> {

        public OfShape(Signal0 updated, CachedElixir cache) {
            super(updated, cache);

            for (Shape shape : Shape.values()) {
                this.addChild(createSlot(ElixirumSounds.UI_BOTTLE_CLINK, shape));
            }

            this.pointer = cache.configured().getStyle().shape();

            selected.connect(shape -> {
                cache.mapStyleSynced(style -> style.withShape(shape));
                this.updated.emit();
            });
        }

        @Override
        protected Shape get() {
            return elixir.configured().getStyle().shape();
        }

        @Override
        protected void render(GuiGraphics graphics, int x, int y, Shape value) {
            var texture = ArsElixirum.identifier("textures/item/elixir/" + value.texture + ".png");
            graphics.blit(texture, x - 1, y - 3, 0f, 0f, 16, 16, 16, 16);
        }

        @Override
        protected boolean isLocked(Shape value) {
            return value.isLocked(ClientAlchemy.INSTANCE.localProfile());
        }

        @Override
        protected Component nameOf(Shape value) {
            return value.displayName();
        }

        @Override
        protected Component descriptionOf(Shape value) {
            if (!isLocked(value)) return null;

            return Component
                    .translatable("style.elixirum.locked", value.mastery)
                    .withStyle(ChatFormatting.GRAY);
        }
    }

    public static class OfChroma extends StylePicker<Chroma> {

        public OfChroma(Signal0 updated, CachedElixir elixir) {
            super(updated, elixir);

            for (Chroma chroma : Chroma.values()) {
                this.addChild(createSlot(ElixirumSounds.UI_BOTTLE_DYE, chroma));
            }

            this.pointer = elixir.configured().getChroma();
            selected.connect(it -> {
                elixir.setChromaSynced(it);
                this.updated.emit();
            });
        }

        @Override
        protected Chroma get() {
            return elixir.configured().getChroma();
        }

        @Override
        protected void render(GuiGraphics graphics, int x, int y, Chroma value) {

            var rgb = value.computeColor(ArsElixirumHelper.getElixirContents(elixir.get()));
            float r = rgb.red();
            float g = rgb.green();
            float b = rgb.blue();

            RenderSystem.setShaderColor(r, g, b, 1f);
            GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.CHROMA, x + 2, y + 2, 12, 12);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        @Override
        protected boolean isLocked(Chroma value) {
            return value.isLocked(ClientAlchemy.INSTANCE.localProfile());
        }

        @Override
        protected Component nameOf(Chroma value) {
            return value.displayName();
        }

        @Override
        protected Component descriptionOf(Chroma value) {
            if (!isLocked(value)) return null;

            return Component
                    .translatable("style.elixirum.locked", value.mastery)
                    .withStyle(ChatFormatting.GRAY);
        }
    }

    private class Slot extends HierarchicalControl {

        private final T value;
        private final Tooltip tooltip;

        public Slot(SoundEvent clickSound, T value) {
            super(0, 0, 15, 15, Component.empty());
            this.value = value;

            this.clickSound = clickSound;
            this.clickAction = ClickAction.leftClick(this::onSelect);

            var text = Component.empty().append(nameOf(value));
            var desc = descriptionOf(value);

            if (desc != null) {
                text.append(CommonComponents.NEW_LINE);
                text.append(desc);
            }

            this.tooltip = Tooltip.create(text);
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {

            if (pointer == value) {
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_WHITE, this);
            } else if (transform.isMouseOver(mouseX, mouseY)) {
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_PURPLE, this);
            }

            StylePicker.this.render(graphics, getX(), getY(), value);

            if (isLocked(value)) {
                RenderSystem.enableBlend();
                GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.LOCK, rect.x() + 1, rect.y() + 1, 13, 13);
                RenderSystem.disableBlend();
            }

            if (transform.isMouseOver(mouseX, mouseY)) {
                AbstractPage.tooltip = this.tooltip;
            }
        }

        @Override
        public void reorganize() {}

        private boolean onSelect(Slot self) {
            if (isLocked(value)) return false;

            pointer = value;
            selected.emit(value);
            return true;
        }
    }
}
