package dev.obscuria.elixirum.common.alchemy.styles;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Locale;

public enum Shape implements StringRepresentable {
    BOTTLE_1(1, 1, "shape/bottle_1", "overlay/bottle_1"),
    BOTTLE_2(2, 3, "shape/bottle_2", "overlay/bottle_2"),
    BOTTLE_3(3, 6, "shape/bottle_3", "overlay/bottle_3"),
    BOTTLE_4(4, 9, "shape/bottle_4", "overlay/bottle_4"),
    BOTTLE_5(5, 12, "shape/bottle_5", "overlay/bottle_5"),
    BOTTLE_6(6, 15, "shape/bottle_6", "overlay/bottle_6"),
    FLASK_1(21, 18, "shape/flask_1", "overlay/flask_1"),
    FLASK_2(22, 21, "shape/flask_2", "overlay/flask_2"),
    FLASK_3(23, 24, "shape/flask_3", "overlay/flask_3"),
    FLASK_4(24, 27, "shape/flask_4", "overlay/flask_4"),
    FLASK_5(25, 30, "shape/flask_5", "overlay/flask_5"),
    FLASK_6(26, 33, "shape/flask_6", "overlay/flask_6"),
    BUBBLE(41, 36, "shape/bubble", "overlay/bubble"),
    TUBE(42, 39, "shape/tube", "overlay/tube"),
    HEART(43, 42, "shape/heart", "overlay/heart"),
    SKULL(44, 45, "shape/skull", "overlay/skull"),
    STAR_1(45, 48, "shape/star_1", "overlay/star_1"),
    STAR_2(46, 51, "shape/star_2", "overlay/star_2");

    public static final Shape DEFAULT;
    public static final Codec<Shape> CODEC;

    public final int id;
    public final int mastery;
    public final String texture;
    public final String overlay;

    Shape(int id, int mastery, String texture, String overlay) {
        this.id = id;
        this.mastery = mastery;
        this.texture = texture;
        this.overlay = overlay;
    }

    public Component displayName() {
        return Component.translatable("style.elixirum.shape." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Shape byId(int id) {
        return Arrays.stream(values()).filter(it -> it.id == id).findFirst().orElse(DEFAULT);
    }

    static {
        DEFAULT = BOTTLE_1;
        CODEC = Codec.INT.xmap(Shape::byId, it -> it.id);
    }
}
