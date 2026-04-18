package dev.obscuria.elixirum.client.screen.widgets;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.fragmentum.util.easing.Easing;
import dev.obscuria.fragmentum.util.easing.EasingFunction;
import dev.obscuria.fragmentum.util.signal.Signal0;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.item.ItemDisplayContext;

public class ElixirOverview extends AbstractWidget {

    private static final EasingFunction PULSE_EASING;
    private final SelectionState<CachedElixir> selection;
    private long lastUpdate = 0;

    public ElixirOverview(SelectionState<CachedElixir> selection, int x, int y) {
        this(selection, new Signal0(), x, y);
    }

    public ElixirOverview(
            SelectionState<CachedElixir> selection,
            Signal0 styleUpdated, int x, int y) {

        super(x, y, 0, 0, CommonComponents.EMPTY);
        this.selection = selection;
        this.selection.listen(this, it -> onUpdate());
        styleUpdated.connect(this, this::onUpdate);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var elixir = selection.get();
        if (elixir.isEmpty()) return;
        var minecraft = Minecraft.getInstance();
        var updateTimer = (Util.getMillis() - lastUpdate) / 500f;

        RenderSystem.enableBlend();
        graphics.pose().pushPose();
        graphics.pose().translate(getX(), getY(), 0f);
        GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.SHINE_X256, -128, -256, 256, 256);
        graphics.pose().popPose();
        RenderSystem.disableBlend();

        Lighting.setupForFlatItems();

        graphics.pose().pushPose();
        graphics.pose().translate(getX(), getY() - 80f, 150f);
        graphics.pose().scale(8f, 8f, 8f);

        graphics.pose().pushPose();
        graphics.pose().scale(16f, -16f, 16f);
        graphics.pose().translate(-0.03125, 0.0, 0.0);
        {
            var pulse = updateTimer > 1f ? 0f : PULSE_EASING.compute(updateTimer);
            var pulseScale = 1f + pulse * 0.075f;
            var pulseOffset = pulse * 0.075f;
            graphics.pose().scale(pulseScale, pulseScale, pulseScale);
            graphics.pose().translate(0.0, pulseOffset, 0.0);
        }
        var model = minecraft.getItemRenderer().getModel(elixir.get(), null, null, 0);
        minecraft.getItemRenderer().render(
                elixir.get(), ItemDisplayContext.GUI, false, graphics.pose(),
                graphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
        graphics.flush();
        graphics.pose().popPose();
        graphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    private void onUpdate() {
        this.lastUpdate = Util.getMillis();
    }

    static {
        PULSE_EASING = Easing.EASE_OUT_CUBIC.mergeOut(Easing.EASE_OUT_ELASTIC, 0.25f);
    }
}
