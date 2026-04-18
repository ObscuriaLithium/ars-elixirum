package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.toolkit.Texture;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import net.minecraft.network.chat.Component;

public class ActionDetails extends AbstractDetails {

    public ActionDetails(CachedElixir elixir) {
        super(Component.literal("Actions"));
        this.addChild(new RemoveButton(elixir));
    }

    private static class RemoveButton extends ButtonControl {

        private final CachedElixir elixir;
        private boolean isAwaitingConfirmation;

        public RemoveButton(CachedElixir elixir) {
            super(Component.literal("Remove"));
            this.elixir = elixir;
        }

        @Override
        public Component getButtonName() {
            return isAwaitingConfirmation
                    ? Component.literal("Confirm")
                    : super.getButtonName();
        }

        @Override
        public Texture pickTexture(boolean isHovered) {
            return isAwaitingConfirmation
                    ? ArsElixirumTextures.buttonRed(isHovered)
                    : super.pickTexture(isHovered);
        }

        @Override
        protected void onClick(ButtonControl self) {
            if (isAwaitingConfirmation) {
                this.isAwaitingConfirmation = false;
                ClientAlchemy.INSTANCE.localProfile().collection().removeRecipe(elixir.configured());
                AbstractPage.lastKind.open();
            } else {
                this.isAwaitingConfirmation = true;
            }
        }
    }
}
