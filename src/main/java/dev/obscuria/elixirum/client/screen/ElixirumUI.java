package dev.obscuria.elixirum.client.screen;

import net.minecraft.network.chat.Component;

public interface ElixirumUI {

    Component DISCOVERIES_HEADER = Component.translatable("ui.elixirum.discoveries.header");
    Component DISCOVERIES_FOOTER = Component.translatable("ui.elixirum.discoveries.footer");
    Component COMPENDIUM_HEADER = Component.translatable("ui.elixirum.compendium.header");

    Component DETAILS_RECIPE = Component.translatable("ui.elixirum.details.recipe");
    Component DETAILS_ASSESSMENT = Component.translatable("ui.elixirum.details.assessment");
    Component DETAILS_MASTERY = Component.translatable("ui.elixirum.details.mastery");
    Component DETAILS_SCORE = Component.translatable("ui.elixirum.details.score");
    Component DETAILS_ACTIONS = Component.translatable("ui.elixirum.details.actions");
    Component DETAILS_INGREDIENTS = Component.translatable("ui.elixirum.details.ingredients");

    Component MASTERY_HINT = Component.translatable("ui.elixirum.mastery.hint");

    Component STYLE_HINT = Component.translatable("ui.elixirum.style.hint");
    Component STYLE_CAP = Component.translatable("ui.elixirum.style.cap");
    Component STYLE_SHAPE = Component.translatable("ui.elixirum.style.shape");
    Component STYLE_CHROMA = Component.translatable("ui.elixirum.style.chroma");

    Component SCORE_BEST_QUALITY = Component.translatable("ui.elixirum.score.best_quality");
    Component SCORE_TIMES_BREWED = Component.translatable("ui.elixirum.score.times_brewed");
    Component SCORE_FABLED_INSTANCES = Component.translatable("ui.elixirum.score.fabled_instances");

    Component BUTTON_SAVE = Component.translatable("ui.elixirum.button.save");
    Component BUTTON_SAVED = Component.translatable("ui.elixirum.button.saved");
    Component BUTTON_REMOVE = Component.translatable("ui.elixirum.button.remove");
    Component BUTTON_CONFIRM = Component.translatable("ui.elixirum.button.confirm");

    Component UNKNOWN = Component.translatable("ui.elixirum.unknown");
    Component NO_DESCRIPTION = Component.translatable("ui.elixirum.no_description");

    Component EFFECT_UNKNOWN = Component.translatable("ui.elixirum.effect.unknown");
    Component EFFECT_UNEXPERIENCED = Component.translatable("ui.elixirum.effect.unexperienced");
    Component EFFECT_BENEFICIAL = Component.translatable("ui.elixirum.effect.beneficial");
    Component EFFECT_HARMFUL = Component.translatable("ui.elixirum.effect.harmful");
    Component EFFECT_NEUTRAL = Component.translatable("ui.elixirum.effect.neutral");
}
