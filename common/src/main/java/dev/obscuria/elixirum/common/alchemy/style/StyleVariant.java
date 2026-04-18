package dev.obscuria.elixirum.common.alchemy.style;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.fragmentum.FragmentumProxy;
import dev.obscuria.fragmentum.network.PayloadCodec;

public record StyleVariant(Cap cap, Shape shape) {

    public static final StyleVariant DEFAULT;
    public static final Codec<StyleVariant> CODEC;
    public static final Codec<StyleVariant> PACKED_CODEC;
    public static final PayloadCodec<StyleVariant> PAYLOAD_CODEC;

    public StyleVariant withCap(Cap cap) {
        return new StyleVariant(cap, shape);
    }

    public StyleVariant withShape(Shape shape) {
        return new StyleVariant(cap, shape);
    }

    public int pack() {
        return (cap.id & 0xFF) | ((shape.id & 0xFF) << 8); //| ((chroma.id & 0xFF) << 16)
    }

    public static StyleVariant unpack(int packed) {
        return new StyleVariant(
                Cap.byId(packed & 0xFF),
                Shape.byId((packed >> 8) & 0xFF)); //Chroma.byId((packed >> 16) & 0xFF)
    }

    static {
        DEFAULT = new StyleVariant(Cap.WOOD, Shape.BOTTLE_1);
        CODEC = RecordCodecBuilder.create(codec -> codec.group(
                Cap.CODEC.optionalFieldOf("cap", Cap.DEFAULT).forGetter(StyleVariant::cap),
                Shape.CODEC.optionalFieldOf("shape", Shape.DEFAULT).forGetter(StyleVariant::shape)
        ).apply(codec, StyleVariant::new));
        PACKED_CODEC = Codec.INT.xmap(StyleVariant::unpack, StyleVariant::pack);
        PAYLOAD_CODEC = PayloadCodec.registryFriendly(PACKED_CODEC, FragmentumProxy::registryAccess);
    }
}
