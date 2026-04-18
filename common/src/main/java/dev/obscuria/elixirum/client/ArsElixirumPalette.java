package dev.obscuria.elixirum.client;

import dev.obscuria.fragmentum.util.color.ARGB;
import dev.obscuria.fragmentum.util.color.Colors;

public interface ArsElixirumPalette {

    ARGB WHITE = Colors.argbOf(0xFFFFFFFF);
    ARGB LIGHT = Colors.argbOf(0xFFB3AEB9);
    ARGB MODERATE = Colors.argbOf(0xFF80788A);
    ARGB DARK = Colors.argbOf(0xFF342D3B);
    ARGB ACCENT = Colors.argbOf(0xFFDE8FFF);

    ARGB EMPHASIS = Colors.argbOf(0xFF8FE1FF);
    ARGB POSITIVE = Colors.argbOf(0xFF8FFFB6);
    ARGB NEGATIVE = Colors.argbOf(0xFFFF8F9C);
    ARGB IMPORTANT = Colors.argbOf(0xFFEC8FFF);
}
