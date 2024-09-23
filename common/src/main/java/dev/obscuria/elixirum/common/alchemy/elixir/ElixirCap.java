package dev.obscuria.elixirum.common.alchemy.elixir;

import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public enum ElixirCap {
    WOOD(1, 0.00, "cap/wood"),
    BLUE_WOOD(2, 0.05, "cap/blue_wood"),
    OPEN(3, 0.10, "cap/open"),
    GLASS(4, 0.15, "cap/glass"),
    AMETHYST(5, 0.20, "cap/amethyst"),
    EMERALD(6, 0.25, "cap/emerald"),
    HEART(7, 0.30, "cap/heart"),
    PIPETTE(8, 0.35, "cap/pipette"),
    LID(9, 0.40, "cap/lid"),
    PIN(10, 0.45, "cap/pin"),
    CROWN(11, 0.50, "cap/crown"),
    SPRAY(12, 0.55, "cap/spray"),
    SCREW_CAP(13, 0.60, "cap/screw_cap"),
    HOLY(14, 0.65, "cap/holy"),
    SKULL(15, 0.70, "cap/skull"),
    WITHER(16, 0.75, "cap/wither"),
    FORGED(17, 0.80, "cap/forged"),
    MOON(18, 0.85, "cap/moon");

    public static final ElixirCap DEFAULT = ElixirCap.WOOD;
    private final int id;
    private final double requiredProgress;
    private final String texture;

    ElixirCap(int id, double requiredProgress, String texture) {
        this.id = id;
        this.requiredProgress = requiredProgress;
        this.texture = texture;
    }

    public static ElixirCap getById(int id) {
        for (var cap : values())
            if (cap.getId() == id)
                return cap;
        return DEFAULT;
    }

    public int getId() {
        return this.id;
    }

    public int getRequiredProgress(int total) {
        return (int) (total * requiredProgress);
    }

    public boolean isLocked(int discovered, int total) {
        return discovered < getRequiredProgress(total);
    }

    public double getPredicate() {
        return getId() / 100.0;
    }

    public String getTexture() {
        return this.texture;
    }

    public String getDescriptionId() {
        return "elixirum.elixir_cap." + toString().toLowerCase();
    }

    public Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(WOOD.getDescriptionId(), "Wood");
        consumer.accept(BLUE_WOOD.getDescriptionId(), "Blue Wood");
        consumer.accept(OPEN.getDescriptionId(), "Open");
        consumer.accept(GLASS.getDescriptionId(), "Glass");
        consumer.accept(AMETHYST.getDescriptionId(), "Amethyst");
        consumer.accept(EMERALD.getDescriptionId(), "Emerald");
        consumer.accept(HEART.getDescriptionId(), "Heart");
        consumer.accept(PIPETTE.getDescriptionId(), "Pipette");
        consumer.accept(LID.getDescriptionId(), "Lid");
        consumer.accept(PIN.getDescriptionId(), "Pin");
        consumer.accept(CROWN.getDescriptionId(), "Crown");
        consumer.accept(SPRAY.getDescriptionId(), "Spray");
        consumer.accept(SCREW_CAP.getDescriptionId(), "Screw Cap");
        consumer.accept(HOLY.getDescriptionId(), "Holy");
        consumer.accept(SKULL.getDescriptionId(), "Skull");
        consumer.accept(WITHER.getDescriptionId(), "Wither");
        consumer.accept(FORGED.getDescriptionId(), "Forged");
        consumer.accept(MOON.getDescriptionId(), "Moon");
    }
}