package dev.obscuria.elixirum.client.screen.section.collection;

import dev.obscuria.elixirum.client.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import dev.obscuria.elixirum.client.screen.widget.Button;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirHolder;
import dev.obscuria.elixirum.registry.ElixirumSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

final class SubRemoveButton extends Button
{
    private static final Component REMOVE = Component.literal("Remove");
    private static final Component CONFIRM = Component.literal("Confirm");
    private boolean confirmNeeded = false;

    public SubRemoveButton()
    {
        super(Component.empty());
        this.setClickSound(ElixirumSounds.UI_CLICK_1);
        this.setClickAction(ClickAction.<SubRemoveButton>left(
                button -> RootCollection.getSelectedHolder().map(this::remove).orElse(false)));
    }

    public void resetConfirm()
    {
        this.confirmNeeded = false;
    }

    @Override
    protected void renderButton(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        final var sprite = isHovered ? PURPLE_SPRITE : GRAY_SPRITE;
        graphics.blitSprite(sprite, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    protected Component getButtonName()
    {
        return confirmNeeded ? CONFIRM : REMOVE;
    }

    private boolean remove(ElixirHolder holder)
    {
        if (!confirmNeeded)
        {
            this.confirmNeeded = true;
        }
        else
        {
            ClientAlchemy.getProfile().removeFromCollection(holder);
            ClientAlchemy.getCache().remove(holder);
            RootCollection.select(null);
            ElixirumScreen.update();
            this.confirmNeeded = false;
        }
        return true;
    }
}
