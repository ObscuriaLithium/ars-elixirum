package dev.obscuria.elixirum.client.screen.widgets.details;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.elixirum.common.alchemy.codex.components.KnownRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ScoreDetails extends AbstractDetails {

    private boolean backgroundFlag = true;

    public ScoreDetails(CachedElixir elixir) {
        super(ElixirumUI.DETAILS_SCORE);
        var entry = findEntry(elixir);
        this.addChild(new Entry(ElixirumUI.SCORE_BEST_QUALITY,
                entry.map(this::bestQuality).orElseGet(this::zero)));
        this.addChild(new Entry(ElixirumUI.SCORE_TIMES_BREWED,
                entry.map(this::timesBrewed).orElseGet(this::zero)));
        this.addChild(new Entry(ElixirumUI.SCORE_FABLED_INSTANCES,
                entry.map(this::fabledInstances).orElseGet(this::zero)));
    }

    private Component bestQuality(KnownRecipes.Entry entry) {
        ;
        return Component.literal(String.valueOf(entry.getBestQuality()));
    }

    private Component timesBrewed(KnownRecipes.Entry entry) {
        return Component.literal(String.valueOf(entry.getBrewCount()));
    }

    private Component fabledInstances(KnownRecipes.Entry entry) {
        return Component.literal(String.valueOf(entry.getFabledInstances()));
    }

    private Optional<KnownRecipes.Entry> findEntry(CachedElixir elixir) {
        var component = ClientAlchemy.localProfile().knownRecipes();
        return Optional.ofNullable(component.entries.get(elixir.recipe().getUuid()));
    }

    private Component zero() {
        return Component.literal("0");
    }

    private class Entry extends HierarchicalControl {

        private final Component name;
        private final Component value;
        private final boolean drawBackground;

        private Entry(Component name, Component value) {
            super(0, 0, 0, 14, CommonComponents.EMPTY);
            this.name = name;
            this.value = value;
            this.drawBackground = ScoreDetails.this.backgroundFlag;
            ScoreDetails.this.backgroundFlag = !ScoreDetails.this.backgroundFlag;
        }

        @Override
        public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {

            var font = Minecraft.getInstance().font;
            if (drawBackground) {
                GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.DARKEST);
                RenderSystem.enableBlend();
                GuiGraphicsUtil.drawShifted(graphics, ArsElixirumTextures.SOLID_WHITE, this);
                RenderSystem.disableBlend();
                GuiGraphicsUtil.resetShaderColor();
            }

            graphics.pose().pushPose();
            graphics.pose().translate(rect.x() + 4, rect.y() + 4, 0f);
            graphics.pose().scale(0.75f, 0.75f, 0.75f);
            graphics.drawString(font, name, 0, 0, ArsElixirumPalette.LIGHT.decimal());
            graphics.pose().popPose();

            graphics.pose().pushPose();
            var x = rect.right() - 4 - font.width(value) * 0.75f;
            graphics.pose().translate(x, rect.y() + 4, 0f);
            graphics.pose().scale(0.75f, 0.75f, 0.75f);
            graphics.drawString(font, value, 0, 0, ArsElixirumPalette.MODERATE.decimal());
            graphics.pose().popPose();
        }

        @Override
        public void reorganize() {}
    }
}
