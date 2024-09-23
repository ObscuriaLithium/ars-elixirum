package dev.obscuria.elixirum.common.alchemy.affix;

import com.mojang.serialization.Codec;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirProcessor;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public enum AffixType implements StringRepresentable {
    ABSOLUTE(AffixType::applyAbsolute),
    NEXT(AffixType::applyNext),
    PREVIOUS(AffixType::applyPrevious),
    OFFENSIVE(AffixType::applyAbsolute),
    DEFENSIVE(AffixType::applyAbsolute),
    ENHANCING(AffixType::applyAbsolute),
    DIMINISHING(AffixType::applyAbsolute);

    public static final Codec<AffixType> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, AffixType> STREAM_CODEC;
    private static final Predicate<Essence> ANY = essence -> true;
    private final Processor processor;

    AffixType(Processor processor) {
        this.processor = processor;
    }

    public Affix create(double modifier) {
        return new Affix(this, modifier);
    }

    public void apply(Affix affix, ElixirProcessor processor, int index) {
        this.processor.apply(affix, processor, index);
    }

    public String getDescriptionId() {
        return "affix.elixirum." + getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this.toString().toLowerCase();
    }

    private static void applyAbsolute(Affix affix, ElixirProcessor processor, int index) {
        processor.listEssences(ANY)
                .forEach(info -> info.addModifier(affix.modifier()));
    }

    private static void applyNext(Affix affix, ElixirProcessor processor, int index) {
        processor.getIngredient(index + 1).stream()
                .flatMap(ingredient -> ingredient.listEssences(ANY))
                .forEach(info -> info.addModifier(affix.modifier()));
    }

    private static void applyPrevious(Affix affix, ElixirProcessor processor, int index) {
        processor.getIngredient(index + 1).stream()
                .flatMap(ingredient -> ingredient.listEssences(ANY))
                .forEach(info -> info.addModifier(affix.modifier()));
    }

    public static void acceptTranslations(BiConsumer<String, String> consumer) {
        consumer.accept(ABSOLUTE.getDescriptionId(), "%s%% weight to all effects");
        consumer.accept(NEXT.getDescriptionId(), "%s%% weight to next ingredient");
        consumer.accept(PREVIOUS.getDescriptionId(), "%s%% weight to previous ingredient");
        consumer.accept(OFFENSIVE.getDescriptionId(), "%s%% weight to offensive effects");
        consumer.accept(DEFENSIVE.getDescriptionId(), "%s%% weight to defensive effects");
        consumer.accept(ENHANCING.getDescriptionId(), "%s%% weight to enhancing effects");
        consumer.accept(DIMINISHING.getDescriptionId(), "%s%% weight to diminishing effects");
    }

    static {
        CODEC = StringRepresentable.fromEnum(AffixType::values);
        STREAM_CODEC = StreamCodec.ofMember(
                (type, buf) -> buf.writeEnum(type),
                (buf) -> buf.readEnum(AffixType.class));
    }

    @FunctionalInterface
    interface Processor {

        void apply(Affix affix, ElixirProcessor processor, int index);
    }
}