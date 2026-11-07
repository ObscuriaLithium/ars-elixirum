package dev.obscuria.elixirum.client.screen.alchemy.guide.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.obscuria.elixirum.client.screen.alchemy.controls.TableControl;
import dev.obscuria.elixirum.client.screen.toolkit.Control;
import dev.obscuria.fragmentum.util.color.ARGB;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public record TableBlock(
        List<ColumnDef> columns,
        List<List<String>> rows,
        HorizontalAlignment hAlign,
        VerticalAlignment vAlign,
        int hPadding,
        int vPadding,
        int minRowHeight,
        float headerScale,
        float contentScale,
        boolean headerCustomFont,
        boolean headerThickSeparator,
        boolean striped,
        String emptyPlaceholder
) implements ContentBlock {

    public static final Codec<TableBlock> CODEC;

    @Override
    public Codec<TableBlock> codec() {
        return CODEC;
    }

    @Override
    public Control instantiate() {
        return new TableControl(this);
    }

    static {
        CODEC = RecordCodecBuilder.create(c -> c.group(
                ColumnDef.CODEC.listOf().fieldOf("columns").forGetter(TableBlock::columns),
                Codec.STRING.listOf().listOf().fieldOf("rows").forGetter(TableBlock::rows),
                HorizontalAlignment.CODEC.optionalFieldOf("h_align", HorizontalAlignment.CENTER).forGetter(TableBlock::hAlign),
                VerticalAlignment.CODEC.optionalFieldOf("v_align", VerticalAlignment.MIDDLE).forGetter(TableBlock::vAlign),
                Codec.INT.optionalFieldOf("h_padding", 4).forGetter(TableBlock::hPadding),
                Codec.INT.optionalFieldOf("v_padding", 4).forGetter(TableBlock::vPadding),
                Codec.INT.optionalFieldOf("min_row_height", 0).forGetter(TableBlock::minRowHeight),
                Codec.FLOAT.optionalFieldOf("header_scale", 0.75f).forGetter(TableBlock::headerScale),
                Codec.FLOAT.optionalFieldOf("content_scale", 0.75f).forGetter(TableBlock::contentScale),
                Codec.BOOL.optionalFieldOf("header_custom_font", true).forGetter(TableBlock::headerCustomFont),
                Codec.BOOL.optionalFieldOf("header_thick_separator", false).forGetter(TableBlock::headerThickSeparator),
                Codec.BOOL.optionalFieldOf("striped", true).forGetter(TableBlock::striped),
                Codec.STRING.optionalFieldOf("empty_placeholder", "-").forGetter(TableBlock::emptyPlaceholder)
        ).apply(c, TableBlock::new));
    }

    public record ColumnDef(
            String header,
            Optional<Float> weight,
            Optional<HorizontalAlignment> hAlign,
            Optional<VerticalAlignment> vAlign,
            Optional<Integer> headerColor,
            Optional<ARGB> contentColor,
            Optional<Integer> textureWidth,
            Optional<Integer> textureHeight
    ) {

        public static final Codec<ColumnDef> CODEC;

        public boolean isIconColumn() {
            return textureWidth.isPresent() && textureHeight.isPresent();
        }

        public HorizontalAlignment resolveHAlign(HorizontalAlignment tableDefault) {
            return hAlign.orElse(tableDefault);
        }

        public VerticalAlignment resolveVAlign(VerticalAlignment tableDefault) {
            return vAlign.orElse(tableDefault);
        }

        static {
            CODEC = RecordCodecBuilder.create(c -> c.group(
                    Codec.STRING.fieldOf("header").forGetter(ColumnDef::header),
                    Codec.FLOAT.optionalFieldOf("weight").forGetter(ColumnDef::weight),
                    HorizontalAlignment.CODEC.optionalFieldOf("h_align").forGetter(ColumnDef::hAlign),
                    VerticalAlignment.CODEC.optionalFieldOf("v_align").forGetter(ColumnDef::vAlign),
                    Codec.INT.optionalFieldOf("header_color").forGetter(ColumnDef::headerColor),
                    ARGB.CODEC.optionalFieldOf("content_color").forGetter(ColumnDef::contentColor),
                    Codec.INT.optionalFieldOf("texture_width").forGetter(ColumnDef::textureWidth),
                    Codec.INT.optionalFieldOf("texture_height").forGetter(ColumnDef::textureHeight)
            ).apply(c, ColumnDef::new));
        }
    }

    public enum HorizontalAlignment implements StringRepresentable {
        LEFT, CENTER, RIGHT;

        public static final Codec<HorizontalAlignment> CODEC = StringRepresentable.fromEnum(HorizontalAlignment::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public enum VerticalAlignment implements StringRepresentable {
        TOP, MIDDLE, BOTTOM;

        public static final Codec<VerticalAlignment> CODEC = StringRepresentable.fromEnum(VerticalAlignment::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}