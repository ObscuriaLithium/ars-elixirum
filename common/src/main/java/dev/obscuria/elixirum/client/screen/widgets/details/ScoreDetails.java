package dev.obscuria.elixirum.client.screen.widgets.details;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.GlobalTransform;
import dev.obscuria.elixirum.client.screen.toolkit.controls.HierarchicalControl;
import dev.obscuria.fragmentum.util.color.ARGB;
import dev.obscuria.fragmentum.util.color.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import static net.minecraft.network.chat.Component.literal;

public class ScoreDetails extends AbstractDetails {

    private boolean backgroundFlag = true;

    public ScoreDetails(CachedElixir elixir) {
        super(literal("Score"));
        this.addChild(new Entry(literal("Best Quality"), literal("" + (int) ArsElixirumHelper.getElixirContents(elixir.get()).quality())));
        this.addChild(new Entry(literal("Times Brewed"), literal("" + ClientAlchemy.INSTANCE.localProfile().statistics().getTimesBrewed(elixir.recipe()))));
        this.addChild(new Entry(literal("Fabled Instances"), literal("0")));
        //this.addChild(new Entry(literal("Arcane Instances"), literal("0")));
        //this.addChild(new Entry(literal("Timeless Instances"), literal("0")));
        //this.addChild(new Entry(literal("Fierce Instances"), literal("0")));
    }

    private class Entry extends HierarchicalControl {

        private static final ARGB BACKGROUND_COLOR = Colors.argbOf(0xFF40384A);
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

            final var font = Minecraft.getInstance().font;
            if (drawBackground) {
                GuiGraphicsUtil.setShaderColor(BACKGROUND_COLOR);
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
            final var x = rect.right() - 4 - font.width(value) * 0.75f;
            graphics.pose().translate(x, rect.y() + 4, 0f);
            graphics.pose().scale(0.75f, 0.75f, 0.75f);
            graphics.drawString(font, value, 0, 0, ArsElixirumPalette.MODERATE.decimal());
            graphics.pose().popPose();
        }

        @Override
        public void reorganize() {}
    }
}
