package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.toolkit.Texture;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.common.network.ServerboundRecipeRemoveRequest;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ActionDetails extends AbstractDetails {

    public ActionDetails(CachedElixir elixir) {
        super(ElixirumUI.DETAILS_ACTIONS);
        this.addChild(new RemoveButton(elixir));
    }

    private static class RemoveButton extends ButtonControl {

        private final CachedElixir elixir;
        private boolean isAwaitingConfirmation;

        public RemoveButton(CachedElixir elixir) {
            super(CommonComponents.EMPTY);
            this.elixir = elixir;
        }

        @Override
        public Component getButtonName() {
            return isAwaitingConfirmation
                    ? ElixirumUI.BUTTON_CONFIRM
                    : ElixirumUI.BUTTON_REMOVE;
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
                ClientAlchemy.localProfile().recipeCollection().remove(elixir.recipe().getUuid());
                FragmentumNetworking.sendToServer(new ServerboundRecipeRemoveRequest(elixir.recipe().getUuid()));
                AbstractPage.lastKind.open();
            } else {
                this.isAwaitingConfirmation = true;
            }
        }
    }
}
