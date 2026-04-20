package dev.obscuria.elixirum.client.screen.widgets.pages.discoveries;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import dev.obscuria.elixirum.client.screen.widgets.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widgets.details.*;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class DiscoveriesPanelDetails extends AbstractDetailsPanel<Essence> {

    protected DiscoveriesPanelDetails(SelectionState<Essence> selection, int x, int y, int width, int height) {
        super(selection, x, y, width, height);
    }

    @Override
    protected Component getPlaceholder() {
        return CommonComponents.EMPTY;
    }

    @Override
    protected Component getDisplayName(Essence target) {
        return !isExperienced(target)
                ? Component.literal("Unknown Effect")
                : target.displayName();
    }

    @Override
    protected boolean isEmpty(Essence target) {
        return target.isEmpty();
    }

    @Override
    protected void rebuild(Essence target) {
        if (target.isEmpty()) return;
        this.content.addChild(ParagraphControl.description(
                isExperienced(target)
                        ? Component.translatable(target.effect().value().getDescriptionId() + ".desc")
                        : Component.literal("You haven't experienced this effect yet, so its properties remain unknown.")));
        this.content.addChild(new IngredientGridDetails(target));
    }

    private boolean isExperienced(Essence target) {
        var profile = ClientAlchemy.INSTANCE.localProfile();
        return profile.knowledge().isExperienced(target.effect().value());
    }
}