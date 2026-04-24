package dev.obscuria.elixirum.client.screen.tooltip.components;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.ArsElixirumPalette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.ArsElixirumTextures;
import dev.obscuria.elixirum.client.screen.GuiGraphicsUtil;
import dev.obscuria.elixirum.client.screen.widgets.pages.AbstractPage;
import dev.obscuria.elixirum.common.alchemy.ingredient.AlchemyProperties;
import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import dev.obscuria.elixirum.common.alchemy.traits.*;
import dev.obscuria.elixirum.common.alchemy.basics.ElixirContents;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;

public class TraitTableComponent implements ClientTooltipComponent {

    private final int width;
    private final int entryWidth;
    private final Entry entry1;
    private final Entry entry2;
    private final Entry entry3;

    public TraitTableComponent(int width, ElixirContents contents) {
        this(width, contents.focus(), contents.form(), contents.risk(), true, true, true);
    }

    public TraitTableComponent(int width, Item item, AlchemyProperties properties) {
        this(width, properties.focus(), properties.application(), properties.risk(),
                DiscoverySystem.isIngredientKnownAsBase(ClientAlchemy.INSTANCE.localProfile(), item),
                DiscoverySystem.isIngredientKnownAsCatalyst(ClientAlchemy.INSTANCE.localProfile(), item),
                DiscoverySystem.isIngredientKnownAsInhibitor(ClientAlchemy.INSTANCE.localProfile(), item));
    }

    public TraitTableComponent(
            int width, Focus focus, Form application, Risk risk,
            boolean isFocusDiscovered,
            boolean isFormDiscovered,
            boolean isRiskDiscovered
    ) {
        this.width = width;
        this.entryWidth = (width - 2) / 3;
        this.entry1 = new Entry(focus, isFocusDiscovered);
        this.entry2 = new Entry(application, isFormDiscovered);
        this.entry3 = new Entry(risk, isRiskDiscovered);
    }

    @Override
    public int getHeight() {
        return Entry.HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        render(graphics, font, x, y, 0, 0, false);
    }

    public void render(GuiGraphics graphics, Font font, int x, int y, int mouseX, int mouseY, boolean hovered) {
        graphics.drawManaged(() -> {
            RenderSystem.enableBlend();
            GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.BLACK.withAlpha(0.2f));
            GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.DECORATION_PANEL_SOLID, x, y, width, Entry.HEIGHT);
            GuiGraphicsUtil.resetShaderColor();
            RenderSystem.disableBlend();
        });

        GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.DARKEST);
        GuiGraphicsUtil.draw(graphics, ArsElixirumTextures.DECORATION_PANEL_OUTLINE, x, y, width, Entry.HEIGHT);
        graphics.vLine(x + entryWidth, y + 3, y + Entry.HEIGHT - 4, 0xFFFFFFFF);
        graphics.vLine(x + entryWidth * 2 + 2, y + 3, y + Entry.HEIGHT - 4, 0xFFFFFFFF);
        GuiGraphicsUtil.resetShaderColor();

        var hoverIndex = hovered ? (mouseX - x) / (entryWidth + 1) : -1;
        this.entry1.render(font, graphics, x, y, entryWidth, hoverIndex == 0);
        this.entry2.render(font, graphics, x + entryWidth + 1, y, entryWidth, hoverIndex == 1);
        this.entry3.render(font, graphics, x + entryWidth * 2 + 2, y, entryWidth, hoverIndex == 2);
    }

    public record Entry(Trait trait, boolean discovered, RGB titleColor, RGB iconColor) {

        public Entry(Trait trait, boolean discovered) {
            this(trait, discovered,
                    discovered
                            ? trait.getColor()
                            : ArsElixirumPalette.MODERATE.toRGB(),
                    discovered
                            ? trait.getColor().lerp(Colors.rgbOf(0xffffff), 0.4f)
                            : ArsElixirumPalette.MODERATE.toRGB());
        }

        public static final int HEIGHT = 36;

        public void render(Font font, GuiGraphics graphics, int x, int y, int width, boolean hovered) {

            var style = Style.EMPTY.withFont(ArsElixirum.FONT);
            var title = trait.getCategoryName().copy().withStyle(style);

            graphics.pose().pushPose();
            graphics.pose().translate(x + width * 0.5f, y + 4, 0);
            graphics.pose().scale(0.5f, 0.5f, 1f);
            graphics.pose().translate(font.width(title) * -0.5f, 0, 0);
            graphics.drawString(font, title, 0, 0, titleColor.decimal(), false);
            graphics.pose().popPose();

            graphics.pose().pushPose();
            graphics.pose().translate(x + width * 0.5f, y + 19, 0);
            graphics.pose().scale(1.1f, 1.1f, 1f);
            graphics.pose().translate(-6.5, -6.5, 0);
            if (hovered) GuiGraphicsUtil.setShaderColor(ArsElixirumPalette.WHITE);
            else GuiGraphicsUtil.setShaderColor(iconColor);
            var icon = discovered ? trait.getIcon() : Icon.LOCK;
            graphics.blit(Icon.TEXTURE, 0, 0, icon.u, icon.v, 13, 13, Icon.TEX_WIDTH, Icon.TEX_HEIGHT);
            GuiGraphicsUtil.resetShaderColor();
            graphics.pose().popPose();

            if (discovered && trait.getProgressShift() > -2) {
                graphics.pose().pushPose();
                graphics.pose().translate(x + width * 0.5f, y + 30, 0);
                graphics.fill(-10, 0, 10, 2, 0xff000000 + ArsElixirumPalette.DARKEST.decimal());
                graphics.fill(0, 0, (int) (trait.getProgressShift() * 10), 2, 0xff000000 + titleColor.decimal());
                graphics.fill(-1, 0, 1, 2, 0xff000000 + ArsElixirumPalette.LIGHT.decimal());
                graphics.pose().popPose();
            }

            if (hovered) {
                var headerStyle = Style.EMPTY.withColor(titleColor.decimal());
                AbstractPage.tooltip = Tooltip.create(Component.empty()
                        .append(trait.getDisplayName().copy().withStyle(headerStyle))
                        .append(CommonComponents.NEW_LINE)
                        .append(trait.getDescription().copy().withStyle(ChatFormatting.GRAY)));
            }
        }
    }
}
