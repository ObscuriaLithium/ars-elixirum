package dev.obscuria.elixirum.common.alchemy;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class ElixirStyles {

    public static Shape defaultShape() {
        return Shape.BOTTLE_1;
    }

    public static Cap defaultCap() {
        return Cap.WOOD;
    }

    public static List<Variant> allVariants() {
        final var index = new AtomicInteger(1);
        return Arrays.stream(Shape.values())
                .flatMap(shape -> Arrays.stream(Cap.values())
                        .map(cap -> new Variant(index.incrementAndGet(), shape, cap)))
                .collect(Collectors.toList());
    }

    public enum Shape {
        BOTTLE_1(1, "shape/bottle_1", "overlay/bottle_1"),
        BOTTLE_2(2, "shape/bottle_2", "overlay/bottle_2"),
        BOTTLE_3(3, "shape/bottle_3", "overlay/bottle_3"),
        BOTTLE_4(4, "shape/bottle_4", "overlay/bottle_4"),
        BOTTLE_5(5, "shape/bottle_5", "overlay/bottle_5"),
        BOTTLE_6(6, "shape/bottle_6", "overlay/bottle_6"),
        FLASK_1(101, "shape/flask_1", "overlay/flask_1"),
        FLASK_2(102, "shape/flask_2", "overlay/flask_2"),
        FLASK_3(103, "shape/flask_3", "overlay/flask_3"),
        FLASK_4(104, "shape/flask_4", "overlay/flask_4"),
        FLASK_5(105, "shape/flask_5", "overlay/flask_5"),
        FLASK_6(106, "shape/flask_6", "overlay/flask_6"),
        TUBE(201, "shape/tube", "overlay/tube"),
        HEART(202, "shape/heart", "overlay/heart"),
        SKULL(203, "shape/skull", "overlay/skull"),
        STAR_1(204, "shape/star_1", "overlay/star_1"),
        STAR_2(205, "shape/star_2", "overlay/star_2");

        private final int id;
        private final String texture;
        private final String overlay;

        Shape(int id, String texture, String overlay) {
            this.id = id;
            this.texture = texture;
            this.overlay = overlay;
        }

        public static Shape getById(int id) {
            for (var shape : values())
                if (shape.getId() == id)
                    return shape;
            return defaultShape();
        }

        public int getId() {
            return this.id;
        }

        public String getTexture() {
            return this.texture;
        }

        public String getOverlay() {
            return this.overlay;
        }
    }

    public enum Cap {
        WOOD(1, "cap/wood"),
        GLASS(2, "cap/glass"),
        AMETHYST(3, "cap/amethyst"),
        EMERALD(4, "cap/emerald"),
        LID(5, "cap/lid"),
        CROWN(6, "cap/crown"),
        SPRAY(7, "cap/spray"),
        SCREW_CAP(8, "cap/screw_cap"),
        HOLY(9, "cap/holy"),
        SKULL(10, "cap/skull"),
        WITHER(11, "cap/wither");

        private final int id;
        private final String texture;

        Cap(int id, String texture) {
            this.id = id;
            this.texture = texture;
        }

        public static Cap getById(int id) {
            for (var cap : values())
                if (cap.getId() == id)
                    return cap;
            return defaultCap();
        }

        public int getId() {
            return this.id;
        }

        public String getTexture() {
            return this.texture;
        }
    }

    public record Variant(int index, Shape shape, Cap cap) {}
}
