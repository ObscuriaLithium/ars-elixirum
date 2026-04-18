package dev.obscuria.elixirum.client.screen.widgets.details;

import dev.obscuria.elixirum.client.screen.toolkit.controls.ParagraphControl;
import dev.obscuria.elixirum.client.screen.toolkit.controls.SpacingControl;
import dev.obscuria.elixirum.common.alchemy.ElixirQuality;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class AssessmentDetails extends AbstractDetails {

    public AssessmentDetails(ItemStack stack) {
        super(Component.literal("Assessment"));
        final var quality = ElixirQuality.of(stack);
        this.addChild(new ParagraphControl(Component.translatable("elixirum.assessment.%s".formatted(quality.getSerializedName()))));
        //if (quality == ElixirQuality.PALE || quality == ElixirQuality.CLOUDY) return;
        //this.addChild(new SpacingControl(5));
        //this.addChild(new ParagraphControl(Component.translatable("elixirum.assessment.mixed_effect")));
    }
}
