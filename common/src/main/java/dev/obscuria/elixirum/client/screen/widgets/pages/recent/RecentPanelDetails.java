package dev.obscuria.elixirum.client.screen.widgets.pages.recent;

import dev.obscuria.elixirum.ArsElixirumHelper;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.toolkit.ClickAction;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.Texture;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ButtonControl;
import dev.obscuria.elixirum.client.screen.widgets.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widgets.details.*;
import dev.obscuria.elixirum.common.network.ServerboundRecipeSaveRequest;
import dev.obscuria.fragmentum.network.FragmentumNetworking;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class RecentPanelDetails extends AbstractDetailsPanel<CachedElixir> {

    protected RecentPanelDetails(SelectionState<CachedElixir> selection, int x, int y, int width, int height) {
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
        this.content.addChild(new ContentDetails(ArsElixirumHelper.getElixirContents(target.get())));
        if (!target.recipe().isEmpty()) this.content.addChild(new RecipeDetails(target.recipe()));
        this.content.addChild(new AssessmentDetails(target.get()));
        this.content.addChild(new MasteryDetails(target));
        this.content.addChild(new ScoreDetails(target));
        this.setFooter(new SaveButton(target));
    }

    static class SaveButton extends ButtonControl {

        private static final Component TEXT_SAVED = Component.literal("Saved");
        private final CachedElixir elixir;

        public SaveButton(CachedElixir elixir) {
            super(Component.literal("Save"));
            this.clickAction = ClickAction.leftClick(this::saveElixir);
            this.elixir = elixir;
        }

        @Override
        public Component getButtonName() {
            return elixir.isInCollection()
                    ? TEXT_SAVED
                    : super.getButtonName();
        }

        @Override
        public Texture pickTexture(boolean isHovered) {
            return elixir.isInCollection()
                    ? ArsElixirumTextures.buttonGreen(isHovered)
                    : super.pickTexture(isHovered);
        }

        private void saveElixir(SaveButton self) {
            if (!ClientAlchemy.INSTANCE.localProfile().collection().saveRecipe(elixir.configured())) return;
            FragmentumNetworking.sendToServer(new ServerboundRecipeSaveRequest(elixir.configured()));
        }
    }
}