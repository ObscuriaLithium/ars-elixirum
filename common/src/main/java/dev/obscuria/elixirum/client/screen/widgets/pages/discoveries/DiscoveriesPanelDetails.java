package dev.obscuria.elixirum.client.screen.widgets.pages.discoveries;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.toolkit.SelectionState;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import dev.obscuria.elixirum.client.screen.widgets.AbstractDetailsPanel;
import dev.obscuria.elixirum.client.screen.widgets.details.*;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

class DiscoveriesPanelDetails extends AbstractDetailsPanel<Essence> {

    protected DiscoveriesPanelDetails(SelectionState<Essence> selection, int x, int y, int width, int height) {
        super(selection, x, y, width, height);
    }

    @Override
    protected Component getPlaceholder() {
        return ElixirumUI.EFFECT_UNEXPERIENCED;
    }

    @Override
    protected Component getDisplayName(Essence target) {
        return isExperienced(target)
                ? target.displayName()
                : ElixirumUI.EFFECT_UNKNOWN;
    }

    @Override
    protected boolean isEmpty(Essence target) {
        return target.isEmpty();
    }

    @Override
    protected void rebuild(Essence target) {
        if (target.isEmpty()) return;
        if (!isExperienced(target)) return;
        this.content.addChild(ParagraphControl.description(getDescription(target)));
        this.content.addChild(new IngredientGridDetails(target));
    }

    private Component getDescription(Essence essence) {
        var baseKey = essence.effect().value().getDescriptionId();
        if (I18n.exists(baseKey + ".desc")) return Component.translatable(baseKey + ".desc");
        if (I18n.exists(baseKey + ".descr")) return Component.translatable(baseKey + ".descr");
        if (I18n.exists(baseKey + ".description")) return Component.translatable(baseKey + ".description");
        return ElixirumUI.NO_DESCRIPTION;
    }

    private boolean isExperienced(Essence target) {
        return ClientAlchemy.localProfile().knownEffects().isKnown(target.effect().value());
    }
}