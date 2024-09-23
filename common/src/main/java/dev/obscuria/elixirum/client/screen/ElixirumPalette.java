package dev.obscuria.elixirum.client.screen;

import net.minecraft.util.FastColor;

public interface ElixirumPalette {
    int WHITE = white(0xFF);
    int DARK = dark(0xFF);
    int MEDIUM = medium(0xFF);
    int LIGHT = light(0xFF);
    int PURPLE = purple(0xFF);

    static int white(int alpha) {
        return FastColor.ARGB32.color(alpha, 0xFF, 0xFF, 0xFF);
    }

    static int dark(int alpha) {
        return FastColor.ARGB32.color(alpha, 0x34, 0x2D, 0x3B);
    }

    static int medium(int alpha) {
        return FastColor.ARGB32.color(alpha, 0x80, 0x78, 0x8A);
    }

    static int light(int alpha) {
        return FastColor.ARGB32.color(alpha, 0xB3, 0xAE, 0xB9);
    }

    static int purple(int alpha) {
        return FastColor.ARGB32.color(alpha, 0xDE, 0x8F, 0xFF);
    }
}
