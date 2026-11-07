package dev.obscuria.elixirum.client.screen.alchemy.details;

import dev.obscuria.elixirum.client.alchemy.cache.CachedElixir;
import dev.obscuria.elixirum.client.screen.ElixirumUI;
import dev.obscuria.elixirum.client.screen.toolkit.controls.text.FootnoteControl;
import dev.obscuria.elixirum.common.alchemy.ElixirQuality;

public final class AssessmentDetails extends AbstractDetails {

    public AssessmentDetails(CachedElixir elixir) {
        super(ElixirumUI.DETAILS_ASSESSMENT);
        this.addChild(new FootnoteControl(ElixirQuality.fromStack(elixir.get()).getAssessment()));
    }
}