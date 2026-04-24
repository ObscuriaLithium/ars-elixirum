package dev.obscuria.elixirum.common.alchemy.traits;

import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.network.chat.Component;

public interface Trait {

    Component getCategoryName();

    Component getDisplayName();

    Component getDescription();

    Icon getIcon();

    RGB getColor();

    float getProgressShift();
}
