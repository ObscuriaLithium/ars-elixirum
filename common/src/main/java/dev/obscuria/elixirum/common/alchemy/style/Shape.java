package dev.obscuria.elixirum.common.alchemy.style;

import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public enum Shape {
    BOTTLE_1(1, 0.00, "shape/bottle_1", "overlay/bottle_1"),
    BOTTLE_2(2, 0.05, "shape/bottle_2", "overlay/bottle_2"),
    BOTTLE_3(3, 0.10, "shape/bottle_3", "overlay/bottle_3"),
    BOTTLE_4(4, 0.15, "shape/bottle_4", "overlay/bottle_4"),
    BOTTLE_5(5, 0.20, "shape/bottle_5", "overlay/bottle_5"),
    BOTTLE_6(6, 0.25, "shape/bottle_6", "overlay/bottle_6"),
    FLASK_1(21, 0.30, "shape/flask_1", "overlay/flask_1"),
    FLASK_2(22, 0.35, "shape/flask_2", "overlay/flask_2"),
    FLASK_3(23, 0.40, "shape/flask_3", "overlay/flask_3"),
    FLASK_4(24, 0.45, "shape/flask_4", "overlay/flask_4"),
    FLASK_5(25, 0.50, "shape/flask_5", "overlay/flask_5"),
    FLASK_6(26, 0.55, "shape/flask_6", "overlay/flask_6"),
    BUBBLE(41, 0.60, "shape/bubble", "overlay/bubble"),
    TUBE(42, 0.65, "shape/tube", "overlay/tube"),
    HEART(43, 0.70, "shape/heart", "overlay/heart"),
    SKULL(44, 0.75, "shape/skull", "overlay/skull"),
    STAR_1(45, 0.80, "shape/star_1", "overlay/star_1"),
    STAR_2(46, 0.85, "shape/star_2", "overlay/star_2");

    public static final Shape DEFAULT = Shape.BOTTLE_1;
    private final int id;
    private final double requiredProgress;
    private final String texture;
    private final String overlay;

    Shape(int id, double requiredProgress, String texture, String overlay) {
        this.id = id;
        this.requiredProgress = requiredProgress;
        this.texture = texture;
        this.overlay = overlay;
    }

    public static Shape getById(int id) {
        for (var shape : values())
            if (shape.getId() == id)
                return shape;
        return DEFAULT;
    }

    public int getRequiredProgress(int total) {
        return (int) (total * requiredProgress);
    }

    public boolean isLocked(int discovered, int total) {
        return discovered < getRequiredProgress(total);
    }

    public int getId() {
        return this.id;
    }

    public double getPredicate() {
        return getId() / 100.0;
    }

    public String getTexture() {
        return this.texture;
    }

    public String getOverlay() {
        return this.overlay;
    }

    public String getDescriptionId() {
        return "elixirum.elixir_shape." + toString().toLowerCase();
    }

    public Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(BOTTLE_1.getDescriptionId(), "Bottle 1");
        consumer.accept(BOTTLE_2.getDescriptionId(), "Bottle 2");
        consumer.accept(BOTTLE_3.getDescriptionId(), "Bottle 3");
        consumer.accept(BOTTLE_4.getDescriptionId(), "Bottle 4");
        consumer.accept(BOTTLE_5.getDescriptionId(), "Bottle 5");
        consumer.accept(BOTTLE_6.getDescriptionId(), "Bottle 6");
        consumer.accept(FLASK_1.getDescriptionId(), "Flask 1");
        consumer.accept(FLASK_2.getDescriptionId(), "Flask 2");
        consumer.accept(FLASK_3.getDescriptionId(), "Flask 3");
        consumer.accept(FLASK_4.getDescriptionId(), "Flask 4");
        consumer.accept(FLASK_5.getDescriptionId(), "Flask 5");
        consumer.accept(FLASK_6.getDescriptionId(), "Flask 6");
        consumer.accept(BUBBLE.getDescriptionId(), "Bubble");
        consumer.accept(TUBE.getDescriptionId(), "Tube");
        consumer.accept(HEART.getDescriptionId(), "Heart");
        consumer.accept(SKULL.getDescriptionId(), "Skull");
        consumer.accept(STAR_1.getDescriptionId(), "Star 1");
        consumer.accept(STAR_2.getDescriptionId(), "Star 2");
    }
}