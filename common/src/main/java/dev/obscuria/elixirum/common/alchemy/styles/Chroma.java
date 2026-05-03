package dev.obscuria.elixirum.common.alchemy.styles;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.client.ArsElixirumClient;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import dev.obscuria.fragmentum.util.easing.Easing;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

public enum Chroma implements StringRepresentable {

    NATURAL(0, 0, ElixirContents::color),
    FOREST(1, 2, it -> ChromaColors.RGB_FOREST),
    MINT(2, 5, it -> ChromaColors.RGB_MINT),
    SEAFOAM(3, 8, it -> ChromaColors.RGB_SEAFOAM),
    SKY(4, 11, it -> ChromaColors.RGB_SKY),
    OCEAN(5, 14, it -> ChromaColors.RGB_OCEAN),
    VIOLET(6, 17, it -> ChromaColors.RGB_VIOLET),
    PURPLE(7, 20, it -> ChromaColors.RGB_PURPLE),
    MAGENTA(8, 23, it -> ChromaColors.RGB_MAGENTA),
    FUCHSIA(9, 26, it -> ChromaColors.RGB_FUCHSIA),
    TERRACOTTA(10, 29, it -> ChromaColors.RGB_TERRACOTTA),
    CARAMEL(11, 32, it -> ChromaColors.RGB_CARAMEL),
    OLIVE(12, 35, it -> ChromaColors.RGB_OLIVE),

    SHIMMERING_FOREST(101, 38, it -> ChromaColors.shimmering(ChromaColors.RGB_FOREST)),
    SHIMMERING_MINT(102, 41, it -> ChromaColors.shimmering(ChromaColors.RGB_MINT)),
    SHIMMERING_SEAFOAM(103, 44, it -> ChromaColors.shimmering(ChromaColors.RGB_SEAFOAM)),
    SHIMMERING_SKY(104, 47, it -> ChromaColors.shimmering(ChromaColors.RGB_SKY)),
    SHIMMERING_OCEAN(105, 50, it -> ChromaColors.shimmering(ChromaColors.RGB_OCEAN)),
    SHIMMERING_VIOLET(106, 53, it -> ChromaColors.shimmering(ChromaColors.RGB_VIOLET)),
    SHIMMERING_PURPLE(107, 56, it -> ChromaColors.shimmering(ChromaColors.RGB_PURPLE)),
    SHIMMERING_MAGENTA(108, 59, it -> ChromaColors.shimmering(ChromaColors.RGB_MAGENTA)),
    SHIMMERING_FUCHSIA(109, 62, it -> ChromaColors.shimmering(ChromaColors.RGB_FUCHSIA)),
    SHIMMERING_TERRACOTTA(110, 65, it -> ChromaColors.shimmering(ChromaColors.RGB_TERRACOTTA)),
    SHIMMERING_CARAMEL(111, 68, it -> ChromaColors.shimmering(ChromaColors.RGB_CARAMEL)),
    SHIMMERING_OLIVE(112, 71, it -> ChromaColors.shimmering(ChromaColors.RGB_OLIVE)),

    PULSING_FOREST(201, 74, it -> ChromaColors.pulsing(ChromaColors.RGB_FOREST)),
    PULSING_MINT(202, 77, it -> ChromaColors.pulsing(ChromaColors.RGB_MINT)),
    PULSING_SEAFOAM(203, 79, it -> ChromaColors.pulsing(ChromaColors.RGB_SEAFOAM)),
    PULSING_SKY(204, 81, it -> ChromaColors.pulsing(ChromaColors.RGB_SKY)),
    PULSING_OCEAN(205, 83, it -> ChromaColors.pulsing(ChromaColors.RGB_OCEAN)),
    PULSING_VIOLET(206, 85, it -> ChromaColors.pulsing(ChromaColors.RGB_VIOLET)),
    PULSING_PURPLE(207, 87, it -> ChromaColors.pulsing(ChromaColors.RGB_PURPLE)),
    PULSING_MAGENTA(208, 89, it -> ChromaColors.pulsing(ChromaColors.RGB_MAGENTA)),
    PULSING_FUCHSIA(209, 91, it -> ChromaColors.pulsing(ChromaColors.RGB_FUCHSIA)),
    PULSING_TERRACOTTA(210, 93, it -> ChromaColors.pulsing(ChromaColors.RGB_TERRACOTTA)),
    PULSING_CARAMEL(211, 95, it -> ChromaColors.pulsing(ChromaColors.RGB_CARAMEL)),
    PULSING_OLIVE(212, 97, it -> ChromaColors.pulsing(ChromaColors.RGB_OLIVE)),

    AURORA(301, 99, it -> ChromaColors.aurora()),
    PRISMATIC(302, 100, it -> ChromaColors.prismatic());

    public final int id;
    public final int mastery;
    private final Function<ElixirContents, RGB> provider;

    Chroma(int id, int mastery, Function<ElixirContents, RGB> provider) {
        this.id = id;
        this.mastery = mastery;
        this.provider = provider;
    }

    public boolean isDefault() {
        return this == NATURAL;
    }

    public String getDescriptionId() {
        return "chroma.elixirum.chroma." + getSerializedName();
    }

    public Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    public RGB computeColor(ElixirContents content) {
        return provider.apply(content);
    }

    public Component displayName() {
        return Component.translatable("style.elixirum.chroma." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static final Codec<Chroma> CODEC = Codec.INT.xmap(Chroma::byId, it -> it.id);

    public static Chroma byId(int id) {
        return Arrays.stream(values())
                .filter(c -> c.id == id)
                .findFirst()
                .orElse(NATURAL);
    }

    public static Chroma unwrap(Either<Integer, Chroma> either) {
        return either.map(Chroma::byId, Function.identity());
    }

    public static final class ChromaColors {

        public static final RGB RGB_FOREST = Colors.rgbOf(0x59A541);
        public static final RGB RGB_MINT = Colors.rgbOf(0x41A55C);
        public static final RGB RGB_SEAFOAM = Colors.rgbOf(0x41A58E);
        public static final RGB RGB_SKY = Colors.rgbOf(0x418AA5);
        public static final RGB RGB_OCEAN = Colors.rgbOf(0x4159A5);
        public static final RGB RGB_VIOLET = Colors.rgbOf(0x5C41A5);
        public static final RGB RGB_PURPLE = Colors.rgbOf(0x8D41A5);
        public static final RGB RGB_MAGENTA = Colors.rgbOf(0xA5418A);
        public static final RGB RGB_FUCHSIA = Colors.rgbOf(0xA54158);
        public static final RGB RGB_TERRACOTTA = Colors.rgbOf(0xA55C41);
        public static final RGB RGB_CARAMEL = Colors.rgbOf(0xA58E41);
        public static final RGB RGB_OLIVE = Colors.rgbOf(0x8AA541);

        public static RGB shimmering(RGB input) {
            float timer = ArsElixirumClient.timer() * 2f;
            float r = input.red();
            float g = input.green();
            float b = input.blue();

            return Colors.rgbOf(
                    (float) (r + 0.15f * Math.cos(timer + Math.PI * 0.33f)),
                    (float) (g + 0.15f * Math.cos(timer + Math.PI * 0.66f)),
                    (float) (b + 0.15f * Math.cos(timer + Math.PI * 0.99f)));
        }

        public static RGB pulsing(RGB input) {
            float timer = ArsElixirumClient.timer() * 5f;
            float offset = -0.1f + 0.2f * (0.5f + 0.5f * (float) Math.sin(timer));

            var hsv = input.toHSV();
            float newH = (hsv.hue() + offset) % 1f;

            return Colors.hsvOf(newH, hsv.saturation(), hsv.value()).toRGB();
        }

        public static RGB aurora() {
            var easing = Easing.EASE_IN_OUT_BACK;
            float timer = ArsElixirumClient.timer() * 3f;
            float offset = easing.compute(0.5f + 0.5f * (float) Math.sin(timer));

            return Colors.rgbOf(1f - offset, offset, 1f);
        }

        public static RGB prismatic() {
            float timer = ArsElixirumClient.timer();

            return Colors.rgbOf(
                    (float) (0.5f + 0.5f * Math.cos(timer)),
                    (float) (0.5f + 0.5f * Math.cos(timer + (2f / 3f) * Math.PI)),
                    (float) (0.5f + 0.5f * Math.cos(timer + (4f / 3f) * Math.PI)));
        }
    }
}
