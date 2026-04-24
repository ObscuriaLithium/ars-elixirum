package dev.obscuria.elixirum.common.alchemy.styles;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Locale;

public enum Cap implements StringRepresentable {
    WOOD(1, 0, "cap/wood"),
    BLUE_WOOD(2, 3, "cap/blue_wood"),
    OPEN(3, 6, "cap/open"),
    GLASS(4, 9, "cap/glass"),
    AMETHYST(5, 12, "cap/amethyst"),
    EMERALD(6, 15, "cap/emerald"),
    HEART(7, 18, "cap/heart"),
    PIPETTE(8, 21, "cap/pipette"),
    LID(9, 24, "cap/lid"),
    PIN(10, 27, "cap/pin"),
    CROWN(11, 30, "cap/crown"),
    SPRAY(12, 33, "cap/spray"),
    SCREW_CAP(13, 36, "cap/screw_cap"),
    HOLY(14, 39, "cap/holy"),
    SKULL(15, 42, "cap/skull"),
    WITHER(16, 45, "cap/wither"),
    FORGED(17, 48, "cap/forged"),
    MOON(18, 51, "cap/moon");

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
