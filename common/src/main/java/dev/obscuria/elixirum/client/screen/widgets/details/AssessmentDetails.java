package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import dev.obscuria.elixirum.common.alchemy.ElixirQuality;

public final class AssessmentDetails extends AbstractDetails {

    public AssessmentDetails(CachedElixir elixir) {
        super(ElixirumUI.DETAILS_ASSESSMENT);
        this.addChild(new ParagraphControl(ElixirQuality.fromStack(elixir.get()).getAssessment()));
    }
}
