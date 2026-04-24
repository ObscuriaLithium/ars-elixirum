package dev.obscuria.elixirum.client.screen.widgets.details;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.SpacingControl;
import dev.obscuria.elixirum.client.screen.tooltip.components.TraitTableComponent;
import dev.obscuria.elixirum.common.alchemy.basics.EffectProvider;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import dev.obscuria.fragmentum.util.easing.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContentDetails extends ListContainer {

    private final ElixirContents contents;
    private final List<Entry> entries;
    private @Nullable Entry hoveredEntry = null;

    @Override
    public boolean hasContents() {
        return true;
    }

    public ContentDetails(ElixirContents contents) {
        this.contents = contents;
        this.entries = compileEntries(contents);

        this.addChild(new Graph());
        this.addChild(new SpacingControl(3));
        this.addChild(new TraitsDisplay(contents));
        this.addChild(new SpacingControl(3));

        for (Entry e : entries) {
            this.addChild(new Effect(e));
        }
    }

    private List<Entry> compileEntries(ElixirContents contents) {
        var effects = new ArrayList<EffectProvider>();
        var sideEffects = new ArrayList<EffectProvider>();

        for (var provider : contents.effects()) {
            if (!provider.isVoided()) effects.add(provider);
            else sideEffects.add(provider);
        }

        var result = new ArrayList<Entry>();
        for (var provider : effects) {
            result.add(new Entry.Single(provider));
        }

        if (!sideEffects.isEmpty() && contents.sideEffectProbability() > 0) {
            result.add(new Entry.Grouped(contents, sideEffects));
        }

        return result;
    }

    private class Graph extends HierarchicalControl {

        private final List<Point> points = new ArrayList<>();
        private int tickCount = 0;

        public Graph() {
            super(0, 0, 0, 0, CommonComponents.EMPTY);
        }

        private int totalAnchors() {
            return entries.size() + 2;
        }

        private double anchorSeparation() {
            return rect.width() / (totalAnchors() - 1.0);
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            if (!transform.isWithinScissor()) return;

            GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.DARKEST);
            GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.OUTLINE_WHITE, this);
            GuiGraphicsUtil.resetShaderColor();

            for (int i = 0; i < entries.size(); i++) {
                double x = rect.left() + (i + 1) * anchorSeparation();
                graphics.vLine((int) x, rect.top(), rect.bottom(), 0xFF40384A);
            }

            for (int i = 1; i <= 4; i++) {
                graphics.hLine(rect.left(), rect.right() - 1,
                        rect.top() + rect.height() / 5 * i,
                        0xFF40384A);
            }

            var matrix = graphics.pose().last().pose();
            float timer = (tickCount + Minecraft.getInstance().getFrameTime()) / 20f;
            float factor = timer < 1f ? Easing.EASE_OUT_ELASTIC.compute(timer) : 1f;
            float bottom = rect.bottom();

            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);

                var c1 = p1.color;
                var c2 = p2.color;

                graphics.drawManaged(() -> {
                    var buffer = graphics.bufferSource().getBuffer(RenderType.guiOverlay());

                    buffer.vertex(matrix, p1.x, lerp(bottom, p1.y, factor), 0f)
                            .color(c1.red(), c1.green(), c1.blue(), 0.5f).endVertex();
                    buffer.vertex(matrix, p1.x, bottom - 2, 0f)
                            .color(c1.red(), c2.green(), c1.blue(), 0f).endVertex();
                    buffer.vertex(matrix, p2.x, bottom - 2, 0f)
                            .color(c2.red(), c2.green(), c2.blue(), 0f).endVertex();
                    buffer.vertex(matrix, p2.x, lerp(bottom, p2.y, factor), 0f)
                            .color(c2.red(), c2.green(), c2.blue(), 0.5f).endVertex();

                    buffer.vertex(matrix, p1.x, lerp(bottom, p1.y, factor), 0f)
                            .color(c1.red(), c1.green(), c1.blue(), 1f).endVertex();
                    buffer.vertex(matrix, p1.x, lerp(bottom, p1.y, factor) + 2, 0f)
                            .color(c1.red(), c1.green(), c1.blue(), 1f).endVertex();
                    buffer.vertex(matrix, p2.x, lerp(bottom, p2.y, factor) + 2, 0f)
                            .color(c2.red(), c2.green(), c2.blue(), 1f).endVertex();
                    buffer.vertex(matrix, p2.x, lerp(bottom, p2.y, factor), 0f)
                            .color(c2.red(), c2.green(), c2.blue(), 1f).endVertex();
                });
            }

            if (transform.isMouseOver(mouseX, mouseY)) {
                int rawIndex = (int) ((mouseX - rect.left() + anchorSeparation() / 2) / anchorSeparation());
                int index = Math.max(1, Math.min(entries.size(), rawIndex));

                hoveredEntry = (index - 1 < entries.size()) ? entries.get(index - 1) : null;

                if (hoveredEntry != null) {
                    int x = (int) (rect.left() + index * anchorSeparation());
                    float y = getEntryY(hoveredEntry);

                    var color = hoveredEntry.color;
                    RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), 1f);
                    GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.DOT, x - 2, (int) y - 1, 5, 5);
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                }
            } else {
                hoveredEntry = null;
            }
        }

        @Override
        public void tick() {
            tickCount++;
            super.tick();
        }

        @Override
        public void reorganize() {
            rect.setWidth(Math.max(rect.width(), 50));
            rect.setHeight(Math.max(rect.height(), 70));

            points.clear();

            List<Point> anchors = new ArrayList<>();

            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                double x = rect.left() + (i + 1) * anchorSeparation();
                float y = getEntryY(entry);
                anchors.add(new Point((float) x, y, entry.color));
            }

            anchors.add(0, new Point(rect.left() + 1, rect.bottom() - 4, Colors.rgbOf(0x342D3B)));
            anchors.add(new Point(rect.right() - 2, rect.bottom() - 4, Colors.rgbOf(0x342D3B)));

            points.addAll(compilePoints(anchors, (int) Math.ceil(64.0 / totalAnchors())));
        }

        private float lerp(float start, float end, float t) {
            return (1 - t) * start + t * end;
        }

        private float smoothstep(float start, float end, float t) {
            return start + (end - start) * (t * t * (3 - 2 * t));
        }

        private int lerpColor(int c1, int c2, float t) {
            int r = (int) smoothstep((c1 >> 16) & 0xFF, (c2 >> 16) & 0xFF, t);
            int g = (int) smoothstep((c1 >> 8) & 0xFF, (c2 >> 8) & 0xFF, t);
            int b = (int) smoothstep(c1 & 0xFF, c2 & 0xFF, t);
            return (r << 16) | (g << 8) | b;
        }

        private List<Point> compilePoints(List<Point> anchors, int resolution) {
            List<Point> result = new ArrayList<>();

            for (int i = 0; i < anchors.size() - 1; i++) {
                Point start = anchors.get(i);
                Point end = anchors.get(i + 1);

                result.add(start);

                for (int j = 1; j < resolution; j++) {
                    float t = j / (float) resolution;
                    float x = lerp(start.x, end.x, t);
                    float y = smoothstep(start.y, end.y, t);
                    int color = lerpColor(start.color.decimal(), end.color.decimal(), t);
                    result.add(new Point(x, y, Colors.rgbOf(color)));
                }
            }

            result.add(anchors.get(anchors.size() - 1));
            return result;
        }

        private float getEntryY(Entry entry) {
            return (rect.bottom() - 3) - (rect.height() - 4) * (entry.weight / 100f);
        }

        private class Point {

            final float x, y;
            final RGB color;

            Point(float x, float y, RGB color) {
                this.x = x;
                this.y = y;
                this.color = color;
            }
        }
    }

    private class Effect extends HierarchicalControl {

        private final Entry entry;
        private MultiLineLabel label = MultiLineLabel.EMPTY;
        private final Font font = Minecraft.getInstance().font;

        public Effect(Entry entry) {
            super(0, 0, 0, 0, Component.empty());
            this.entry = entry;
            this.setUpdateFlags(UPDATE_BY_WIDTH);
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            if (!transform.isWithinScissor()) return;

            if (hoveredEntry == entry) {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.1f);
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.SOLID_LIGHT, this);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
            }

            boolean isSmall = rect.height() <= 20;

            entry.renderIcon(graphics, rect.x(), rect.y() + rect.height() / 2);

            graphics.pose().pushPose();
            graphics.pose().translate(rect.x() + 21, rect.y() + (isSmall ? 3 : 2), 0);
            graphics.pose().scale(0.75f, 0.75f, 0.75f);
            label.renderLeftAligned(graphics, 0, 0, 9, ArsElixirumPalette.LIGHT.decimal());
            graphics.pose().popPose();

            graphics.pose().pushPose();
            graphics.pose().translate(rect.x() + 21, rect.bottom() - (isSmall ? 9 : 8), 0);
            graphics.pose().scale(0.75f, 0.75f, 0.75f);
            graphics.drawString(font, entry.getSecondaryText(), 0, 0, ArsElixirumPalette.MODERATE.decimal());
            graphics.pose().popPose();
        }

        @Override
        public void reorganize() {
            this.label = MultiLineLabel.create(font, entry.getPrimaryText(), rect.width() - 10);
            this.rect.setHeight(Math.max(20, 2 + (int) Math.ceil(7.5f * label.getLineCount() - 1) + 8));
        }
    }

    public static abstract class Entry {

        public final float weight;
        public final RGB color;

        public Entry(float weight, RGB color) {
            this.weight = weight;
            this.color = color;
        }

        public abstract Component getPrimaryText();

        public abstract Component getSecondaryText();

        public abstract void renderIcon(GuiGraphics graphics, int x, int y);

        public static class Single extends Entry {

            private final EffectProvider effect;

            public Single(EffectProvider effect) {
                super((int) effect.quality(), Colors.rgbOf(effect.mobEffect().getColor()));
                this.effect = effect;
            }

            @Override
            public Component getPrimaryText() {
                return effect.displayNameWithPotency();
            }

            @Override
            public Component getSecondaryText() {
                return effect.statusOrDuration();
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y) {
                var textures = Minecraft.getInstance().getMobEffectTextures();
                graphics.blit(x, y - 9, 0, 18, 18, textures.get(effect.mobEffect()));
            }
        }

        public static class Grouped extends Entry {

            private final List<EffectProvider> effects;
            private final double probability;

            public Grouped(ElixirContents contents, List<EffectProvider> effects) {
                super(weightOf(effects), ArsElixirumPalette.MODERATE.toRGB());
                this.effects = effects;
                this.probability = contents.sideEffectProbability();
            }

            @Override
            public Component getPrimaryText() {
                return effects.size() == 1
                        ? Component.translatable("ui.elixirum.side_effects.one", effects.size())
                        : Component.translatable("ui.elixirum.side_effects.many", effects.size());
            }

            @Override
            public Component getSecondaryText() {
                return Component.translatable(
                        "ui.elixirum.side_effects.chance",
                        "%.0f%%".formatted(probability * 100f));
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y) {
                var textures = Minecraft.getInstance().getMobEffectTextures();

                if (effects.size() >= 4) {
                    graphics.blit(x + 9, y, 0, 9, 9, textures.get(effects.get(3).mobEffect()));
                    graphics.blit(x, y, 0, 9, 9, textures.get(effects.get(2).mobEffect()));
                    graphics.blit(x + 9, y - 9, 0, 9, 9, textures.get(effects.get(1).mobEffect()));
                    graphics.blit(x, y - 9, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
                } else if (effects.size() >= 3) {
                    graphics.blit(x + 9, y, 0, 9, 9, textures.get(effects.get(2).mobEffect()));
                    graphics.blit(x, y, 0, 9, 9, textures.get(effects.get(1).mobEffect()));
                    graphics.blit(x + 5, y - 9, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
                } else if (effects.size() >= 2) {
                    graphics.blit(x + 9, y - 4, 0, 9, 9, textures.get(effects.get(1).mobEffect()));
                    graphics.blit(x, y - 4, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
                } else if (!effects.isEmpty()) {
                    graphics.blit(x + 5, y - 4, 0, 9, 9, textures.get(effects.get(0).mobEffect()));
                }
            }

            private static float weightOf(List<EffectProvider> effects) {
                var weight = 0.0;
                for (var effect : effects)
                    weight += effect.quality();
                return Math.min(100f, (float) weight);
            }
        }
    }

    private static class TraitsDisplay extends HierarchicalControl {

        private final ElixirContents contents;
        private TraitTableComponent table;

        public TraitsDisplay(ElixirContents contents) {
            super(0, 0, 0, TraitTableComponent.Entry.HEIGHT, CommonComponents.EMPTY);
            this.contents = contents;
            this.table = new TraitTableComponent(10, contents);
            this.setUpdateFlags(UPDATE_BY_WIDTH);
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
            table.render(
                    graphics, Minecraft.getInstance().font, rect.x(), rect.y(),
                    mouseX, mouseY, transform.isMouseOver(mouseX, mouseY));
        }

        @Override
        public void reorganize() {
            this.table = new TraitTableComponent(rect.width(), contents);
        }
    }
}