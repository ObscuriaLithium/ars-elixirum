package dev.obscuria.elixirum.common.alchemy.traits;

import dev.obscuria.elixirum.ArsElixirum;
import net.minecraft.resources.ResourceLocation;

public enum Icon {
    BALANCE(0, 0),
    FOCUS_POTENCY(13, 0),
    FOCUS_DURATION(26, 0),
    APPLICATION_POTABLE(39, 0),
    APPLICATION_EXPLOSIVE(0, 13),
    APPLICATION_LINGERING(13, 13),
    RISK_LOW(26, 13),
    RISK_HIGH(39, 13),
    LOCK(0, 26);

    public static final ResourceLocation TEXTURE = ArsElixirum.identifier("textures/gui/icons.png");
    public static final int TEX_WIDTH = 52;
    public static final int TEX_HEIGHT = 52;

    public final int u;
    public final int v;

    Icon(int u, int v) {
        this.u = u;
        this.v = v;
    }
}
