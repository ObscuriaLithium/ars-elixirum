package dev.obscuria.elixirum.common.alchemy.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.api.alchemy.components.StyleVariant;
import dev.obscuria.elixirum.common.alchemy.styles.Cap;
import dev.obscuria.elixirum.common.alchemy.styles.Shape;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;

public record _StyleVariant(Cap cap, Shape shape) implements StyleVariant {

    public static final StyleVariant DEFAULT;
    public static final StyleVariant ELIXIR;
    public static final StyleVariant SPLASH_ELIXIR;
    public static final StyleVariant LINGERING_ELIXIR;

    public static final Codec<StyleVariant> CODEC;
    public static final Codec<StyleVariant> PACKED_CODEC;
    public static final PayloadCodec<StyleVariant> PAYLOAD_CODEC;

    public boolean isDefault() {
        return cap == Cap.DEFAULT && shape == Shape.DEFAULT;
    }

    static {
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Cap.CODEC.optionalFieldOf("cap", Cap.DEFAULT).forGetter(StyleVariant::cap),
                Shape.CODEC.optionalFieldOf("shape", Shape.DEFAULT).forGetter(StyleVariant::shape)
        ).apply(codec, StyleVariant::of));
        PACKED_CODEC = Codec.INT.xmap(StyleVariant::unpack, StyleVariant::pack);
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(PACKED_CODEC, FragmentumProxy::registryAccess);

        DEFAULT = StyleVariant.of(Cap.WOOD, Shape.BOTTLE_1);
        ELIXIR = StyleVariant.of(Cap.DEFAULT, Shape.FLASK_2);
        SPLASH_ELIXIR = StyleVariant.of(Cap.LID, Shape.FLASK_2);
        LINGERING_ELIXIR = StyleVariant.of(Cap.CROWN, Shape.FLASK_2);
    }
}
