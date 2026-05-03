package dev.obscuria.elixirum.client.screen.tooltips.components;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.api.alchemy.AlchemyProperties;
import dev.obscuria.elixirum.api.alchemy.components.ElixirContents;
import dev.obscuria.elixirum.client.Palette;
import dev.obscuria.elixirum.client.alchemy.ClientAlchemy;
import dev.obscuria.elixirum.client.screen.Textures;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import dev.obscuria.elixirum.client.screen.toolkit.GuiToolkit;
import dev.obscuria.elixirum.common.alchemy.systems.DiscoverySystem;
import dev.obscuria.elixirum.common.alchemy.traits.*;
import dev.obscuria.fragmentum.util.color.Colors;
import dev.obscuria.fragmentum.util.color.RGB;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
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
        this(width, properties.focus(), properties.form(), properties.risk(),
                DiscoverySystem.isIngredientKnownAsBase(ClientAlchemy.localProfile(), item),
                DiscoverySystem.isIngredientKnownAsCatalyst(ClientAlchemy.localProfile(), item),
                DiscoverySystem.isIngredientKnownAsInhibitor(ClientAlchemy.localProfile(), item));
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

    @Override public int getHeight() {return Entry.HEIGHT;}

    @Override public int getWidth(Font font) {return width;}

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        render(graphics, font, x, y, 0, 0, false);
    }

    public void render(GuiGraphics graphics, Font font, int x, int y, int mouseX, int mouseY, boolean hovered) {

        graphics.drawManaged(() -> {
            RenderSystem.enableBlend();
            GuiToolkit.setShaderColor(Palette.BLACK, 0.2f);
            GuiToolkit.draw(graphics, Textures.DECORATION_PANEL_SOLID, x, y, width, Entry.HEIGHT);
            GuiToolkit.resetShaderColor();
            RenderSystem.disableBlend();
        });

        GuiToolkit.setShaderColor(Palette.DARKEST);
        GuiToolkit.draw(graphics, Textures.DECORATION_PANEL_OUTLINE, x, y, width, Entry.HEIGHT);
        GuiToolkit.resetShaderColor();

        var hoverIndex = hovered ? (mouseX - x) / (entryWidth + 1) : -1;

        this.entry1.render(font, graphics, x, y, entryWidth, hoverIndex == 0, false);
        this.entry2.render(font, graphics, x + entryWidth + 1, y, entryWidth, hoverIndex == 1, true);
        this.entry3.render(font, graphics, x + entryWidth * 2 + 2, y, entryWidth, hoverIndex == 2, true);
    }

    public record Entry(Trait trait, boolean discovered, RGB titleColor, RGB iconColor) {

        public Entry(Trait trait, boolean discovered) {
            this(trait, discovered,
                    discovered ? trait.getColor()
                            : Palette.MODERATE.toRGB(),
                    discovered ? trait.getColor().lerp(Colors.rgbOf(0xffffff), 0.4f)
                            : Palette.MODERATE.toRGB());
        }

        public static final int HEIGHT = 36;

        public void render(Font font, GuiGraphics graphics,
                           int x, int y, int width, boolean hovered, boolean drawLeftDivider) {

            final int col = titleColor.decimal() & 0x00FFFFFF;
            final float h = HEIGHT;
            final var mat = graphics.pose().last().pose();

            if (drawLeftDivider) {
                graphics.drawManaged(() -> {
                    var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    buf.vertex(mat, x - 1, y, 0f).color(255, 255, 255, 0).endVertex();
                    buf.vertex(mat, x - 1, y + HEIGHT * 0.5f, 0f).color(255, 255, 255, 60).endVertex();
                    buf.vertex(mat, x, y + HEIGHT * 0.5f, 0f).color(255, 255, 255, 60).endVertex();
                    buf.vertex(mat, x, y, 0f).color(255, 255, 255, 0).endVertex();

                    buf.vertex(mat, x - 1, y + HEIGHT * 0.5f, 0f).color(255, 255, 255, 60).endVertex();
                    buf.vertex(mat, x - 1, y + HEIGHT - 1, 0f).color(255, 255, 255, 0).endVertex();
                    buf.vertex(mat, x, y + HEIGHT - 1, 0f).color(255, 255, 255, 0).endVertex();
                    buf.vertex(mat, x, y + HEIGHT * 0.5f, 0f).color(255, 255, 255, 60).endVertex();
                });
                graphics.vLine(x - 1, y, y + HEIGHT - 1, (col | 0x3C000000));
            }

            RenderSystem.enableBlend();
            if (discovered) {
                int r = (col >> 16) & 0xFF;
                int g = (col >> 8) & 0xFF;
                int b = col & 0xFF;
                graphics.drawManaged(() -> {
                    var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    buf.vertex(mat, x, y + 1, 0f).color(r, g, b, 0).endVertex();
                    buf.vertex(mat, x, y + HEIGHT - 1, 0f).color(r, g, b, 28).endVertex();
                    buf.vertex(mat, x + width, y + HEIGHT - 1, 0f).color(r, g, b, 28).endVertex();
                    buf.vertex(mat, x + width, y + 1, 0f).color(r, g, b, 0).endVertex();
                });
            }

            if (hovered && discovered) {
                int r = (col >> 16) & 0xFF;
                int g = (col >> 8) & 0xFF;
                int b = col & 0xFF;
                graphics.drawManaged(() -> {
                    var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                    buf.vertex(mat, x, y, 0f).color(r, g, b, 0).endVertex();
                    buf.vertex(mat, x, y + HEIGHT, 0f).color(r, g, b, 40).endVertex();
                    buf.vertex(mat, x + width, y + HEIGHT, 0f).color(r, g, b, 40).endVertex();
                    buf.vertex(mat, x + width, y, 0f).color(r, g, b, 0).endVertex();
                });
            }
            RenderSystem.disableBlend();

            var style = Style.EMPTY.withFont(ArsElixirum.FONT);
            var title = trait.getCategoryName().copy().withStyle(style);
            int nameAlpha = hovered ? 0xFF : 0xBB;
            int nameColor = (nameAlpha << 24) | col;

            graphics.pose().pushPose();
            graphics.pose().translate(x + width * 0.5f, y + 4, 0);
            graphics.pose().scale(0.5f, 0.5f, 1f);
            graphics.pose().translate(font.width(title) * -0.5f, 0, 0);
            graphics.drawString(font, title, 0, 0, nameColor, false);
            graphics.pose().popPose();

            float iconScale = hovered ? 1.25f : 1.1f;
            graphics.pose().pushPose();
            graphics.pose().translate(x + width * 0.5f, y + 19, 0);
            graphics.pose().scale(iconScale, iconScale, 1f);
            graphics.pose().translate(-6.5, -6.5, 0);
            if (hovered) GuiToolkit.setShaderColor(Palette.WHITE);
            else GuiToolkit.setShaderColor(iconColor);
            var icon = discovered ? trait.getIcon() : Icon.LOCK;
            graphics.blit(Icon.TEXTURE, 0, 0, icon.u, icon.v, 13, 13, Icon.TEX_WIDTH, Icon.TEX_HEIGHT);
            GuiToolkit.resetShaderColor();
            graphics.pose().popPose();

            if (discovered && trait.getProgressShift() > -2) {
                float progress = trait.getProgressShift();
                float filled = progress * 10f;

                int barW = 20;
                int barH = 2;

                graphics.pose().pushPose();
                graphics.pose().translate(x + width * 0.5f, y + 30, 0);


                var lmat = graphics.pose().last().pose();

                graphics.fill(
                        -barW / 2, 0, barW / 2, barH,
                        0xFF000000 | Palette.DARKEST.decimal());

                if (Math.abs(filled) > 0.5f) {
                    int x1 = (int) Math.max(-barW / 2f, Math.min(0, filled));
                    int x2 = (int) Math.max(0, Math.min(barW / 2f, filled));

                    boolean goingRight = filled > 0;
                    int dimCol = (col & 0x00FFFFFF) | 0x55000000;
                    int fullCol = (col & 0x00FFFFFF) | 0xFF000000;
                    int leftCol = goingRight ? dimCol : fullCol;
                    int rightCol = goingRight ? fullCol : dimCol;

                    int lr = (leftCol >> 16) & 0xFF, lg = (leftCol >> 8) & 0xFF, lb = leftCol & 0xFF, la = (leftCol >> 24) & 0xFF;
                    int rr = (rightCol >> 16) & 0xFF, rg = (rightCol >> 8) & 0xFF, rb = rightCol & 0xFF, ra = (rightCol >> 24) & 0xFF;

                    graphics.drawManaged(() -> {
                        var buf = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
                        buf.vertex(lmat, x1, 0, 0f).color(lr, lg, lb, la).endVertex();
                        buf.vertex(lmat, x1, barH, 0f).color(lr, lg, lb, la).endVertex();
                        buf.vertex(lmat, x2, barH, 0f).color(rr, rg, rb, ra).endVertex();
                        buf.vertex(lmat, x2, 0, 0f).color(rr, rg, rb, ra).endVertex();
                    });

                    int tip = goingRight ? x2 - 1 : x1;
                    graphics.fill(tip, -1, tip + 1, barH + 1, (col & 0x00FFFFFF) | 0x55000000);
                    graphics.fill(tip, 0, tip + 1, barH, 0xFF000000 | col);
                }

                graphics.fill(-1, 0, 1, barH, 0xFF000000 | Palette.LIGHT.decimal());

                graphics.pose().popPose();
            }

            if (hovered) {
                var headerStyle = Style.EMPTY.withColor(titleColor.decimal());
                AlchemyScreen.tooltip = Tooltip.create(Component.empty()
                        .append(trait.getDisplayName().copy().withStyle(headerStyle))
                        .append(CommonComponents.NEW_LINE)
                        .append(trait.getDescription().copy().withStyle(ChatFormatting.GRAY)));
            }
        }
    }
}
