package dev.obscuria.elixirum.client.screen.section.recent;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.widget.Button;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

final class SubSaveButton extends Button
{
    private static final Component SAVE = Component.literal("Save");
    private static final Component SAVED = Component.literal("Saved");

    public SubSaveButton()
    {
        super(Component.empty());
        this.setClickSound(ElixirumSounds.UI_CLICK_1);
        this.setClickAction(ClickAction.<SubSaveButton>left(
                button -> RootRecent.getSelectedHolder().map(this::save).orElse(false)));
    }

    @Override
    protected void renderButton(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        final var sprite = isSaved() ? GREEN_SPRITE : isHovered ? PURPLE_SPRITE : GRAY_SPRITE;
        graphics.blitSprite(sprite, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    protected Component getButtonName()
    {
        return isSaved() ? SAVED : SAVE;
    }

    private boolean isSaved()
    {
        return RootRecent.getSelectedHolder()
                .map(holder -> ClientAlchemy.getProfile().isOnCollection(holder.getRecipe()))
                .orElse(false);
    }

    private boolean save(ElixirHolder holder)
    {
        return ClientAlchemy.getProfile().addToCollection(holder);
    }
}
