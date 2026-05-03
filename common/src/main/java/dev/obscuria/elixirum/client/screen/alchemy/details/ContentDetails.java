package dev.obscuria.elixirum.client.screen.alchemy.details;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.api.alchemy.EffectProvider;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.elixirum.client.screen.toolkit.GuiContext;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.client.screen.toolkit.containers.ListContainer;
import dev.obscuria.elixirum.client.screen.toolkit.controls.layout.HSpacingControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Label;
import dev.obscuria.elixirum.client.screen.tooltips.components.TraitTableComponent;
import dev.obscuria.elixirum.common.alchemy.ElixirQuality;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import dev.obscuria.fragmentum.util.easing.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContentDetails extends ListContainer {

    private final List<Entry> entries;
    private @Nullable Entry hoveredEntry = null;

    public ContentDetails(ElixirContents contents) {
        this.entries = compileEntries(contents);
        this.addChild(new Graph());
        this.addChild(new HSpacingControl(3));
        this.addChild(new TraitsDisplay(contents));
        this.addChild(new HSpacingControl(3));
        for (var entry : entries) this.addChild(new Effect(entry));
    }

    private List<Entry> compileEntries(ElixirContents contents) {
        var effects = new ArrayList<EffectProvider>();
        var sideEffects = new ArrayList<EffectProvider>();
        for (var provider : contents.effects()) {
            if (!provider.isVoided()) effects.add(provider);
            else sideEffects.add(provider);
        }
        var result = new ArrayList<Entry>();
        for (var provider : effects) result.add(new Entry.Single(provider));
        if (!sideEffects.isEmpty() && contents.sideEffectProbability() > 0)
            result.add(new Entry.Grouped(contents, sideEffects));
        return result;
    }

    @Override
    protected boolean hasOwnContent() {
        return true;
    }

    private static String qualityLabel(float quality) {
        if (quality <= 0) return "Mixture";
        return ElixirQuality.fromQuality(quality).getDisplayName().getString();
    }

    private class Graph extends Control {

        private static final RGB ANCHOR_COLOR = Colors.rgbOf(0x2A2535);

        private final List<Point> points = new ArrayList<>();
        private final List<Float> anchorYs = new ArrayList<>();
        private int tickCount = 0;

        public Graph() {
            setSizeHints(SIZE_HINT_WIDTH);
        }

        private int totalAnchors() {
            return entries.size() + 2;
        }

        private double anchorSep() {
            return getWidth() / (totalAnchors() - 1.0);
        }

        private float qualityToLocalY(float quality) {
            float usable = getHeight() - 6f;
            return (getHeight() - 3f) - usable * (quality / 100f);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            if (!context.isVisible(this)) return;

            final Font fnt = Minecraft.getInstance().font;
            final float frameT = Minecraft.getInstance().getFrameTime();
            final float timer = (tickCount + frameT) / 20f;
            final float factor = timer < 1f ? Easing.EASE_OUT_ELASTIC.compute(timer) : 1f;
            final float bottom = getY() + getHeight();

            context.pushModulate(Palette.DARKEST);
            GuiToolkit.draw(graphics, Textures.OUTLINE_WHITE, this);
            context.popModulate();

            for (int q : new int[]{25, 50, 75, 100}) {
                int gy = getY() + (int) qualityToLocalY(q);
                drawDashedHLine(graphics, getX() + 2, getX() + getWidth() - 2, gy, 0x18FFFFFF);
            }

            graphics.fill(getX() + 2, getY() + 2, getX() + 5, getY() + 3, 0x33FFFFFF);
            graphics.fill(getX() + 2, getY() + getHeight() - 3, getX() + 5, getY() + getHeight() - 2, 0x33FFFFFF);

            RenderSystem.enableBlend();
            var matrix = graphics.pose().last().pose();

            for (int i = 0; i < points.size() - 1; i++) {
                var p1 = points.get(i);
                var p2 = points.get(i + 1);
                var c1 = p1.color;
                var c2 = p2.color;
                float ay1 = lerp(bottom, getY() + p1.y, factor);
                float ay2 = lerp(bottom, getY() + p2.y, factor);

                graphics.drawManaged(() -> {
                    var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    buf.vertex(matrix, p1.x, ay1, 0f).color(c1.red(), c1.green(), c1.blue(), 0.40f).endVertex();
                    buf.vertex(matrix, p1.x, bottom - 1, 0f).color(c1.red(), c1.green(), c1.blue(), 0.00f).endVertex();
                    buf.vertex(matrix, p2.x, bottom - 1, 0f).color(c2.red(), c2.green(), c2.blue(), 0.00f).endVertex();
                    buf.vertex(matrix, p2.x, ay2, 0f).color(c2.red(), c2.green(), c2.blue(), 0.40f).endVertex();
                });

                graphics.drawManaged(() -> {
                    var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    buf.vertex(matrix, p1.x, ay1, 0f).color(c1.red(), c1.green(), c1.blue(), 1f).endVertex();
                    buf.vertex(matrix, p1.x, ay1 + 2, 0f).color(c1.red(), c1.green(), c1.blue(), 1f).endVertex();
                    buf.vertex(matrix, p2.x, ay2 + 2, 0f).color(c2.red(), c2.green(), c2.blue(), 1f).endVertex();
                    buf.vertex(matrix, p2.x, ay2, 0f).color(c2.red(), c2.green(), c2.blue(), 1f).endVertex();
                });
            }
            RenderSystem.disableBlend();

            for (int i = 0; i < entries.size(); i++) {
                var entry = entries.get(i);
                float targetY = anchorYs.isEmpty() ? qualityToLocalY(entry.weight) : anchorYs.get(i);
                float ax = (float) (getX() + (i + 1) * anchorSep());
                float ay = lerp(bottom, getY() + targetY, factor);

                graphics.pose().pushPose();
                graphics.pose().translate(ax - 2, ay - 1, 0);
                context.pushModulate(entry.color);
                GuiToolkit.draw(graphics, Textures.DOT, 0, 0, 5, 5);
                context.popModulate();
                graphics.pose().popPose();
            }

            boolean isOver = context.isMouseOver(this, mouseX, mouseY);
            if (isOver) {
                int rawIndex = (int) ((mouseX - getX() + anchorSep() / 2) / anchorSep());
                int index = Math.max(1, Math.min(entries.size(), rawIndex));
                hoveredEntry = (index - 1 < entries.size()) ? entries.get(index - 1) : null;
            } else {
                hoveredEntry = null;
            }

            if (hoveredEntry != null) {
                int idx = entries.indexOf(hoveredEntry);
                float targetY = anchorYs.isEmpty() ? qualityToLocalY(hoveredEntry.weight) : anchorYs.get(idx);
                float ax = (float) (getX() + (idx + 1) * anchorSep());
                float ay = lerp(bottom, getY() + targetY, factor);
                int col = hoveredEntry.color.decimal() & 0x00FFFFFF;

                int sw = Math.max(3, (int) anchorSep() - 6);
                graphics.fill((int) ax - sw / 2, getY() + 2,
                        (int) ax + sw / 2, getY() + getHeight() - 2,
                        col | 0x18000000);

                graphics.pose().pushPose();
                graphics.pose().translate(0, ay, 0);
                drawDashedHLine(graphics, getX() + 2, (int) ax - 3, 1, col | 0x88000000);
                graphics.fill(getX() + 1, 0, getX() + 4, 2, col | 0xDD000000);
                graphics.pose().popPose();

                String qStr = "~" + (int) hoveredEntry.weight;
                float ly = ay - 4;
                ly = Math.max(getY() + 2, Math.min(ly, getY() + getHeight() - 7));
                graphics.pose().pushPose();
                float clampedLy = Math.max(getY() + 2, Math.min(ay - 4, getY() + getHeight() - 7));
                graphics.pose().translate(getX() + 5, clampedLy, 0);
                graphics.pose().scale(0.5f, 0.5f, 1f);
                graphics.drawString(fnt, qStr, 1, 1, 0x88000000, false);
                graphics.drawString(fnt, qStr, 0, 0, col | 0xFF000000, false);
                graphics.pose().popPose();

                if (!hoveredEntry.isSideGroup) {
                    String tierName = qualityLabel(hoveredEntry.weight);
                    float nameW = fnt.width(tierName) * 0.55f;
                    float tlx = ax - nameW * 0.5f;
                    tlx = Math.max(getX() + 2, Math.min(tlx, getX() + getWidth() - 2 - nameW));
                    float tly = getY() + getHeight() - 8;
                    graphics.pose().pushPose();
                    graphics.pose().translate(tlx, tly, 0);
                    graphics.pose().scale(0.55f, 0.55f, 1f);
                    graphics.drawString(fnt, tierName, 1, 1, 0x88000000, false);
                    graphics.drawString(fnt, tierName, 0, 0, col | 0xFF000000, false);
                    graphics.pose().popPose();
                }

                graphics.pose().pushPose();
                graphics.pose().translate(ax - 3, ay - 2, 0);
                context.pushModulate(hoveredEntry.color);
                GuiToolkit.draw(graphics, Textures.DOT, 0, 0, 7, 7);
                context.popModulate();
                graphics.pose().popPose();
            }

            graphics.pose().pushPose();
            graphics.pose().translate(getX() + 2, getY() + getHeight() - 6, 0);
            graphics.pose().scale(0.5f, 0.5f, 1f);
            graphics.drawString(fnt, "0", 0, 0, 0x55FFFFFF, false);
            graphics.pose().popPose();

            graphics.pose().pushPose();
            graphics.pose().translate(getX() + 2, getY() + 2, 0);
            graphics.pose().scale(0.5f, 0.5f, 1f);
            graphics.drawString(fnt, "100", 0, 0, 0x88FFD700, false);
            graphics.pose().popPose();
        }

        private void drawDashedHLine(GuiGraphics g, int x1, int x2, int y, int color) {
            for (int x = x1; x < x2; x += 4)
                g.fill(x, y, Math.min(x + 2, x2), y + 1, color);
        }

        @Override
        public void tick() {
            tickCount++;
            super.tick();
        }

        @Override
        protected void measure() {
            setMeasuredSize(50, 80);
        }

        @Override
        protected void layout() {
            points.clear();
            anchorYs.clear();

            var anchors = new ArrayList<Point>();

            for (var entry : entries) {
                float localY = qualityToLocalY(entry.weight);
                anchorYs.add(localY);
                float ax = (float) (getX() + (entries.indexOf(entry) + 1) * anchorSep());
                anchors.add(new Point(ax, localY, entry.color));
            }

            anchors.add(0, new Point((float) getX() + 1,
                    qualityToLocalY(0), ANCHOR_COLOR));
            anchors.add(new Point((float) (getX() + getWidth() - 2),
                    qualityToLocalY(0), ANCHOR_COLOR));

            points.addAll(compilePoints(anchors, (int) Math.ceil(64.0 / totalAnchors())));
        }

        private List<Point> compilePoints(List<Point> anchors, int resolution) {
            var result = new ArrayList<Point>();
            for (int i = 0; i < anchors.size() - 1; i++) {
                var start = anchors.get(i);
                var end = anchors.get(i + 1);
                result.add(start);
                for (int j = 1; j < resolution; j++) {
                    float t = j / (float) resolution;
                    result.add(new Point(
                            lerp(start.x, end.x, t),
                            smoothstep(start.y, end.y, t),
                            Colors.rgbOf(lerpColor(start.color.decimal(), end.color.decimal(), t))));
                }
            }
            result.add(anchors.get(anchors.size() - 1));
            return result;
        }

        private float lerp(float a, float b, float t) {return a + (b - a) * t;}

        private float smoothstep(float a, float b, float t) {
            float s = t * t * (3 - 2 * t);
            return a + (b - a) * s;
        }

        private int lerpColor(int c1, int c2, float t) {
            int r = (int) smoothstep((c1 >> 16) & 0xFF, (c2 >> 16) & 0xFF, t);
            int g = (int) smoothstep((c1 >> 8) & 0xFF, (c2 >> 8) & 0xFF, t);
            int b = (int) smoothstep(c1 & 0xFF, c2 & 0xFF, t);
            return (r << 16) | (g << 8) | b;
        }

        private record Point(float x, float y, RGB color) {}
    }

    private class Effect extends Control {

        private final Entry entry;
        private Label label = Label.EMPTY;

        public Effect(Entry entry) {
            super(0, 0, 0, 0, Component.empty());
            this.entry = entry;
            setSizeHints(SIZE_HINT_WIDTH);
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            if (!context.isVisible(this)) return;
            if (hoveredEntry == entry) GuiToolkit.draw(graphics, context, Textures.SOLID_LIGHT, this, 0.1f);
            graphics.pose().pushPose();
            graphics.pose().translate(getX(), getCenterY(), 0);
            this.entry.renderIcon(graphics, 1, 0);
            this.label.drawVCentered(graphics, 21, 0, 0xffffffff);
            graphics.pose().popPose();
        }

        @Override
        protected void measure() {
            this.label = Label.create(Component.empty()
                            .append(GuiToolkit.dye(entry.getPrimaryText(), Palette.LIGHT))
                            .append(CommonComponents.NEW_LINE)
                            .append(GuiToolkit.dye(entry.getSecondaryText(), Palette.MODERATE)),
                    getWidth() - 20, GuiToolkit.PARAGRAPH_SCALE);
            setRequiredHeight(Math.max(20, label.getHeight()));
        }
    }

    public static abstract class Entry {

        public final float weight;
        public final RGB color;
        public final boolean isSideGroup;

        protected Entry(float weight, RGB color, boolean isSideGroup) {
            this.weight = weight;
            this.color = color;
            this.isSideGroup = isSideGroup;
        }

        public abstract Component getPrimaryText();

        public abstract Component getSecondaryText();

        public abstract void renderIcon(GuiGraphics graphics, int x, int y);

        public static class Single extends Entry {

            private final EffectProvider effect;

            public Single(EffectProvider effect) {
                super((int) effect.quality(),
                        Colors.rgbOf(effect.mobEffect().getColor()),
                        false);
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
                var tex = Minecraft.getInstance().getMobEffectTextures();
                graphics.blit(x, y - 9, 0, 18, 18, tex.get(effect.mobEffect()));
            }
        }

        public static class Grouped extends Entry {

            private final List<EffectProvider> effects;
            private final double probability;

            public Grouped(ElixirContents contents, List<EffectProvider> effects) {
                super(weightOf(effects), Palette.MODERATE.toRGB(), true);
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
                var tex = Minecraft.getInstance().getMobEffectTextures();
                if (effects.size() >= 4) {
                    graphics.blit(x + 9, y, 0, 9, 9, tex.get(effects.get(3).mobEffect()));
                    graphics.blit(x, y, 0, 9, 9, tex.get(effects.get(2).mobEffect()));
                    graphics.blit(x + 9, y - 9, 0, 9, 9, tex.get(effects.get(1).mobEffect()));
                    graphics.blit(x, y - 9, 0, 9, 9, tex.get(effects.get(0).mobEffect()));
                } else if (effects.size() == 3) {
                    graphics.blit(x + 9, y, 0, 9, 9, tex.get(effects.get(2).mobEffect()));
                    graphics.blit(x, y, 0, 9, 9, tex.get(effects.get(1).mobEffect()));
                    graphics.blit(x + 5, y - 9, 0, 9, 9, tex.get(effects.get(0).mobEffect()));
                } else if (effects.size() == 2) {
                    graphics.blit(x + 9, y - 4, 0, 9, 9, tex.get(effects.get(1).mobEffect()));
                    graphics.blit(x, y - 4, 0, 9, 9, tex.get(effects.get(0).mobEffect()));
                } else if (!effects.isEmpty()) {
                    graphics.blit(x + 5, y - 4, 0, 9, 9, tex.get(effects.get(0).mobEffect()));
                }
            }

            private static float weightOf(List<EffectProvider> effects) {
                double w = 0;
                for (var e : effects) w += e.quality();
                return Math.min(100f, (float) w);
            }
        }
    }

    private static class TraitsDisplay extends Control {

        private final ElixirContents contents;
        private TraitTableComponent table;

        public TraitsDisplay(ElixirContents contents) {
            super(0, 0, 0, TraitTableComponent.Entry.HEIGHT, CommonComponents.EMPTY);
            this.contents = contents;
            this.table = new TraitTableComponent(10, contents);
            this.setSizeHints(SIZE_HINT_WIDTH);
        }

        @Override
        public void render(GuiGraphics graphics, GuiContext context, int mouseX, int mouseY) {
            table.render(graphics, Minecraft.getInstance().font,
                    getX(), getY(), mouseX, mouseY,
                    context.isMouseOver(this, mouseX, mouseY));
        }

        @Override
        protected void layout() {
            this.table = new TraitTableComponent(getWidth(), contents);
        }
    }
}