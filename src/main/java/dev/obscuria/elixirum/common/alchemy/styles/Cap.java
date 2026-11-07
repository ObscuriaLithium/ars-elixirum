package dev.obscuria.elixirum.common.alchemy.styles;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Locale;

public enum Cap implements StringRepresentable {
    WOOD(1, 1, "cap/wood"),
    BLUE_WOOD(2, 2, "cap/blue_wood"),
    OPEN(3, 5, "cap/open"),
    GLASS(4, 8, "cap/glass"),
    AMETHYST(5, 11, "cap/amethyst"),
    EMERALD(6, 14, "cap/emerald"),
    HEART(7, 17, "cap/heart"),
    PIPETTE(8, 20, "cap/pipette"),
    LID(9, 23, "cap/lid"),
    PIN(10, 26, "cap/pin"),
    CROWN(11, 29, "cap/crown"),
    SPRAY(12, 32, "cap/spray"),
    SCREW_CAP(13, 35, "cap/screw_cap"),
    HOLY(14, 38, "cap/holy"),
    SKULL(15, 41, "cap/skull"),
    WITHER(16, 44, "cap/wither"),
    FORGED(17, 47, "cap/forged"),
    MOON(18, 50, "cap/moon");

    public static final Cap DEFAULT;
    public static final Codec<Cap> CODEC;

    public final int id;
    public final int mastery;
    public final String texture;

    Cap(int id, int mastery, String texture) {
        this.id = id;
        this.mastery = mastery;
        this.texture = texture;
    }

    public Component displayName() {
        return Component.translatable("style.elixirum.cap." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Cap byId(int id) {
        return Arrays.stream(values()).filter(it -> it.id == id).findFirst().orElse(DEFAULT);
    }

    static {
        DEFAULT = WOOD;
        CODEC = Codec.INT.xmap(Cap::byId, it -> it.id);
    }
}
