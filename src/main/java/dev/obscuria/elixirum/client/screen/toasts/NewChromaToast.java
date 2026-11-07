package dev.obscuria.elixirum.client.screen.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.common.alchemy.styles.Chroma;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public final class NewChromaToast extends NewStyleToast<Chroma> {

    private static final Component TITLE_SINGLE = Component.translatable("toast.elixirum.new_chroma.single");
    private static final Component TITLE_MULTIPLE = Component.translatable("toast.elixirum.new_chroma.multiple");

    public static void addOrUpdate(ToastComponent component, Chroma chroma) {
        NewStyleToast.addOrUpdate(component, chroma, NewChromaToast.class, () -> new NewChromaToast(chroma));
    }

    private NewChromaToast(Chroma chroma) {
        addItem(chroma);
    }

    @Override
    protected Component getTitle() {
        return isSingle() ? TITLE_SINGLE : TITLE_MULTIPLE;
    }

    @Override
    protected Component getDisplayName(Chroma item) {
        return item.displayName();
    }

    @Override
    protected void renderIcon(GuiGraphics graphics, ToastComponent component, Chroma chroma) {
        var rgb = chroma.computeColor(ElixirContents.water());
        graphics.pose().translate(16.5, 16.5, 0);
        graphics.pose().scale(1.5f, 1.5f, 1.5f);
        RenderSystem.setShaderColor(rgb.red(), rgb.green(), rgb.blue(), 1f);
        GuiToolkit.draw(graphics, Textures.CHROMA, -6, -6, 12, 12);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
}
