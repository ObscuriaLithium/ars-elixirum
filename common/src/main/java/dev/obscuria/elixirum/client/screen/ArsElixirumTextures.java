package dev.obscuria.elixirum.client.screen;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.screen.toolkit.Texture;
import net.minecraft.resources.ResourceLocation;

public interface ArsElixirumTextures {

    ResourceLocation PANEL_SHEET = ArsElixirum.identifier("textures/gui/panel_sheet.png");
    ResourceLocation ATLAS_DECORATIONS = ArsElixirum.identifier("textures/gui/atlas/decorations.png");

    Texture PANEL = Texture.nineSliced(PANEL_SHEET, 128, 128, 32, 0, 0);

    Texture GENE_FRAME = Texture.fixed(ArsElixirum.identifier("textures/gui/gene_frame.png"), 24, 24);
    Texture GENE_FRAME_HOVERED = Texture.fixed(ArsElixirum.identifier("textures/gui/gene_frame_hovered.png"), 24, 24);
    Texture ARCHOGENE_FRAME = Texture.fixed(ArsElixirum.identifier("textures/gui/archogene_frame.png"), 24, 24);
    Texture ARCHOGENE_FRAME_HOVERED = Texture.fixed(ArsElixirum.identifier("textures/gui/archogene_frame_hovered.png"), 24, 24);
    Texture LOCK = Texture.fixed(ArsElixirum.identifier("textures/gui/lock.png"), 24, 24);

    Texture SOLID_LIGHT = Texture.nineSliced(PANEL_SHEET, 62, 63, 6, 129, 0);
    Texture SOLID_DARK = Texture.nineSliced(PANEL_SHEET, 62, 62, 6, 193, 0);
    Texture SOLID_PURPLE = Texture.nineSliced(PANEL_SHEET, 62, 62, 6, 129, 65);
    Texture SOLID_WHITE = Texture.nineSliced(PANEL_SHEET, 62, 62, 6, 193, 65);

    Texture OUTLINE_LIGHT = Texture.nineSliced(PANEL_SHEET, 62, 62, 6, 129, 129);
    Texture OUTLINE_DARK = Texture.nineSliced(PANEL_SHEET, 62, 62, 6, 193, 129);
    Texture OUTLINE_PURPLE = Texture.nineSliced(PANEL_SHEET, 62, 62, 6, 129, 193);
    Texture OUTLINE_WHITE = Texture.nineSliced(PANEL_SHEET, 62, 62, 6, 193, 193);

    Texture DECORATION_PANEL_OUTLINE = Texture.nineSliced(ATLAS_DECORATIONS, 32, 32, 6, 0, 0);
    Texture DECORATION_PANEL_SOLID = Texture.nineSliced(ATLAS_DECORATIONS, 32, 32, 6, 32, 0);

    Texture BUTTON_GRAY = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 1, 129);
    Texture BUTTON_GREEN = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 33, 129);
    Texture BUTTON_PURPLE = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 65, 129);
    Texture BUTTON_RED = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 97, 129);

    Texture BUTTON_GRAY_OUTLINE = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 1, 161);
    Texture BUTTON_GREEN_OUTLINE = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 33, 161);
    Texture BUTTON_PURPLE_OUTLINE = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 65, 161);
    Texture BUTTON_RED_OUTLINE = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 97, 161);

    Texture TABLE_TOP = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 1, 193);
    Texture TABLE_MIDDLE = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 33, 193);
    Texture TABLE_BOTTOM = Texture.nineSliced(PANEL_SHEET, 30, 30, 6, 65, 193);
    Texture PROGRESS = Texture.nineSliced(PANEL_SHEET, 30, 30, 2, 97, 193);

    Texture DOT = Texture.fixed(ArsElixirum.identifier("textures/gui/dot.png"), 5, 5, 0, 0, 6, 6);
    Texture CHROMA = Texture.fixed(ArsElixirum.identifier("textures/gui/chroma.png"), 12, 12);
    Texture ARROW_UP = Texture.fixed(ArsElixirum.identifier("textures/gui/arrow_up.png"), 8, 8);
    Texture ARROW_DOWN = Texture.fixed(ArsElixirum.identifier("textures/gui/arrow_down.png"), 8, 8);
    Texture CHECK_MARK = Texture.fixed(ArsElixirum.identifier("textures/gui/check_mark.png"), 6, 6);
    Texture SHINE_X64 = Texture.stretched(ArsElixirum.identifier("textures/gui/shine_x64.png"));
    Texture SHINE_X256 = Texture.stretched(ArsElixirum.identifier("textures/gui/shine_x256.png"));

    static Texture buttonGray(boolean isHovered) {
        return isHovered ? BUTTON_GRAY_OUTLINE : BUTTON_GRAY;
    }

    static Texture buttonGreen(boolean isHovered) {
        return isHovered ? BUTTON_GREEN_OUTLINE : BUTTON_GREEN;
    }

    static Texture buttonPurple(boolean isHovered) {
        return isHovered ? BUTTON_PURPLE_OUTLINE : BUTTON_PURPLE;
    }

    static Texture buttonRed(boolean isHovered) {
        return isHovered ? BUTTON_RED_OUTLINE : BUTTON_RED;
    }
}
