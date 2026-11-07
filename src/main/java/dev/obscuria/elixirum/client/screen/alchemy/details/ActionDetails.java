package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Texture;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
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
        protected Texture pickTexture(boolean isHovered) {
            return isAwaitingConfirmation
                    ? Textures.buttonRed(isHovered)
                    : super.pickTexture(isHovered);
        }

        @Override
        protected Component pickButtonName() {
            return isAwaitingConfirmation
                    ? ElixirumUI.BUTTON_CONFIRM
                    : ElixirumUI.BUTTON_REMOVE;
        }

        @Override
        protected void onClick() {
            if (isAwaitingConfirmation) {
                this.isAwaitingConfirmation = false;
                ClientAlchemy.localProfile().recipeCollection().remove(elixir.recipe().uuid());
                FragmentumNetworking.sendToServer(new ServerboundRecipeRemoveRequest(elixir.recipe().uuid()));
                AlchemyScreen.lastPage.open();
            } else {
                this.isAwaitingConfirmation = true;
            }
        }
    }
}
