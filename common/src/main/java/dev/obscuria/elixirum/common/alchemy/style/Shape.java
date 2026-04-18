package dev.obscuria.elixirum.common.alchemy.style;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.profiles.AlchemyProfileView;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Locale;

public enum Shape implements StringRepresentable {
    BOTTLE_1(1, 0, "shape/bottle_1", "overlay/bottle_1"),
    BOTTLE_2(2, 1, "shape/bottle_2", "overlay/bottle_2"),
    BOTTLE_3(3, 4, "shape/bottle_3", "overlay/bottle_3"),
    BOTTLE_4(4, 7, "shape/bottle_4", "overlay/bottle_4"),
    BOTTLE_5(5, 10, "shape/bottle_5", "overlay/bottle_5"),
    BOTTLE_6(6, 13, "shape/bottle_6", "overlay/bottle_6"),
    FLASK_1(21, 16, "shape/flask_1", "overlay/flask_1"),
    FLASK_2(22, 19, "shape/flask_2", "overlay/flask_2"),
    FLASK_3(23, 22, "shape/flask_3", "overlay/flask_3"),
    FLASK_4(24, 25, "shape/flask_4", "overlay/flask_4"),
    FLASK_5(25, 28, "shape/flask_5", "overlay/flask_5"),
    FLASK_6(26, 31, "shape/flask_6", "overlay/flask_6"),
    BUBBLE(41, 34, "shape/bubble", "overlay/bubble"),
    TUBE(42, 37, "shape/tube", "overlay/tube"),
    HEART(43, 40, "shape/heart", "overlay/heart"),
    SKULL(44, 43, "shape/skull", "overlay/skull"),
    STAR_1(45, 46, "shape/star_1", "overlay/star_1"),
    STAR_2(46, 49, "shape/star_2", "overlay/star_2");

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

    public boolean isLocked(AlchemyProfileView profile) {
        return profile.mastery().masteryLevel().get() < mastery;
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
