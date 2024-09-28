package dev.obscuria.elixirum.common.alchemy.essence;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.function.BiConsumer;

public enum EssenceCategory implements StringRepresentable {
    NONE,
    OFFENSIVE,
    DEFENSIVE,
    ENHANCING,
    DIMINISHING;

    public static final Codec<EssenceCategory> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, EssenceCategory> STREAM_CODEC;

    public Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    public String getDescriptionId() {
        return "elixirum.essence_category." + getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this.toString().toLowerCase();
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(NONE.getDescriptionId(), "None");
        consumer.accept(OFFENSIVE.getDescriptionId(), "Offensive");
        consumer.accept(DEFENSIVE.getDescriptionId(), "Defensive");
        consumer.accept(ENHANCING.getDescriptionId(), "Enhancing");
        consumer.accept(DIMINISHING.getDescriptionId(), "Diminishing");
    }

    static {
        CODEC = StringRepresentable.fromEnum(EssenceCategory::values);
        STREAM_CODEC = StreamCodec.ofMember(
                (value, buf) -> buf.writeEnum(value),
                buf -> buf.readEnum(EssenceCategory.class));
    }
}
