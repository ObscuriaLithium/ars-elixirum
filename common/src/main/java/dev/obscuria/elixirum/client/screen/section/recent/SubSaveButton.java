package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.widget.Button;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirStyle;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Optional;

final class SubSaveButton extends Button {
    private static final Component SAVE = Component.literal("Save");
    private static final Component SAVED = Component.literal("Saved");

    public SubSaveButton() {
        super(Component.empty());
        this.setClickSound(ElixirumSounds.UI_CLICK_1.holder());
        this.setClickAction(ClickAction.<SubSaveButton>left(button -> {
            final var elixir = RootRecent.getSelected().orElse(null);
            if (elixir == null) return false;
            if (ClientAlchemy.getProfile().isSaved(elixir.recipe())) return false;
            ClientAlchemy.getProfile().savePage(new ElixirHolder(
                    elixir.recipe(),
                    ElixirStyle.get(elixir.stack()),
                    Optional.empty(),
                    Optional.empty()));
            elixir.saved().set(null);
            return true;
        }));
    }

    @Override
    protected void renderButton(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY) {
        final var sprite = isSaved() ? GREEN_SPRITE : isHovered ? PURPLE_SPRITE : GRAY_SPRITE;
        graphics.blitSprite(sprite, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    protected Component getButtonName() {
        return isSaved() ? SAVED : SAVE;
    }

    private boolean isSaved() {
        return RootRecent.getSelected()
                .map(elixir -> elixir.saved().get())
                .orElse(false);
    }
}
