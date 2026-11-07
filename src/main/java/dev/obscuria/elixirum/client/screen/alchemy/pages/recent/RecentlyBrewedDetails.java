package dev.obscuria.elixirum.client.screen.alchemy.pages.recent;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.details.*;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
import dev.obscuria.elixirum.client.screen.alchemy.containers.AbstractDetailsPage;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Texture;
import dev.obscuria.elixirum.common.network.ServerboundRecipeSaveRequest;
import dev.obscuria.elixirum.api.ArsElixirumAPI;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class RecentlyBrewedDetails extends AbstractDetailsPage<CachedElixir> {

    protected RecentlyBrewedDetails(Selection<CachedElixir> selection, int x, int y, int width, int height) {
        super(selection, x, y, width, height);
    }

    @Override
    protected Component getPlaceholder() {
        return CommonComponents.EMPTY;
    }

    @Override
    protected Component getDisplayName(CachedElixir target) {
        return target.displayName();
    }

    @Override
    protected boolean isEmpty(CachedElixir target) {
        return target.isEmpty();
    }

    @Override
    protected void rebuild(CachedElixir target) {
        if (target.isEmpty()) return;
        this.content.addChild(new ContentDetails(ArsElixirumAPI.getElixirContents(target.get())));
        if (!target.recipe().isEmpty()) this.content.addChild(new RecipeDetails(target.recipe()));
        this.content.addChild(new AssessmentDetails(target));
        this.content.addChild(new MasteryDetails(target));
        this.content.addChild(new ScoreDetails(target));
        this.setFooter(new SaveButton(target));
    }

    static class SaveButton extends ButtonControl {

        private final CachedElixir elixir;

        public SaveButton(CachedElixir elixir) {
            super(CommonComponents.EMPTY);
            this.clickHandler = ClickHandler.leftClick(SaveButton.class, this::saveElixir);
            this.elixir = elixir;
        }

        @Override
        protected Texture pickTexture(boolean isHovered) {
            return elixir.isInCollection()
                    ? Textures.buttonGreen(isHovered)
                    : super.pickTexture(isHovered);
        }

        @Override
        protected Component pickButtonName() {
            return elixir.isInCollection()
                    ? ElixirumUI.BUTTON_SAVED
                    : ElixirumUI.BUTTON_SAVE;
        }

        private boolean saveElixir(SaveButton self) {
            if (!ClientAlchemy.localProfile().recipeCollection().save(elixir.configured())) return false;
            FragmentumNetworking.sendToServer(new ServerboundRecipeSaveRequest(elixir.configured()));
            return true;
        }
    }
}
