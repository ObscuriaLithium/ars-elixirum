package dev.obscuria.elixirum.client.screen.alchemy.pages.discoveries;

import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.alchemy.containers.AbstractDetailsPage;
import dev.obscuria.elixirum.client.screen.alchemy.details.IngredientGridDetails;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.ParagraphControl;
import dev.obscuria.elixirum.client.screen.toolkit.tools.Selection;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.TextControl;
import dev.obscuria.elixirum.common.alchemy.basics.Essence;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class DiscoveriesDetails extends AbstractDetailsPage<DiscoveryTarget> {

    protected DiscoveriesDetails(Selection<DiscoveryTarget> selection, int x, int y, int width, int height) {
        super(selection, x, y, width, height);
    }

    @Override
    protected Component getPlaceholder() {
        return ElixirumUI.EFFECT_UNEXPERIENCED;
    }

    @Override
    protected Component getDisplayName(DiscoveryTarget target) {
        if (target.isEmpty()) return CommonComponents.EMPTY;
        return isExperienced(target.get())
                ? target.get().displayName()
                : ElixirumUI.EFFECT_UNKNOWN;
    }

    @Override
    protected boolean isEmpty(DiscoveryTarget target) {
        return target.isEmpty();
    }

    @Override
    protected void rebuild(DiscoveryTarget target) {
        if (target.isEmpty()) return;
        if (!isExperienced(target.get())) return;
        this.content.addChild(new ParagraphControl(getDescription(target.get())));
        this.content.addChild(new IngredientGridDetails(target.get()));
    }

    private Component getDescription(Essence essence) {
        var baseKey = essence.effect().value().getDescriptionId();
        if (I18n.exists(baseKey + ".desc")) return Component.translatable(baseKey + ".desc");
        if (I18n.exists(baseKey + ".descr")) return Component.translatable(baseKey + ".descr");
        if (I18n.exists(baseKey + ".description")) return Component.translatable(baseKey + ".description");
        return ElixirumUI.NO_DESCRIPTION;
    }

    private boolean isExperienced(Essence essence) {
        return ClientAlchemy.localProfile().knownEffects().isKnown(essence.effect().value());
    }
}