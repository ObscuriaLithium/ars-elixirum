package dev.obscuria.elixirum.client.screen.widget;

import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import dev.obscuria.elixirum.client.screen.HierarchicalWidget;
import dev.obscuria.elixirum.client.screen.tool.GlobalTransform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class Text extends HierarchicalWidget
{
    private final Font font;
    private MultiLineLabel label;
    private Component content;
    private Style style;
    private float scale;
    private boolean centered;

    public Text()
    {
        this(0, 0, 0, 0);
    }

    public Text(int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty());
        this.setUpdateFlags(UPDATE_BY_WIDTH);
        this.font = Minecraft.getInstance().font;
        this.label = MultiLineLabel.EMPTY;
        this.content = Component.empty();
        this.style = Style.EMPTY;
        this.scale = 1f;
        this.centered = false;
    }

    public Text setContent(Component content)
    {
        this.content = content;
        this.setChanged(true);
        return this;
    }

    public Text setStyle(Style style)
    {
        this.style = style;
        this.setChanged(true);
        return this;
    }

    public Text setScale(float scale)
    {
        this.scale = scale;
        this.setChanged(true);
        return this;
    }

    public Text setCentered(boolean centered)
    {
        this.centered = centered;
        this.setChanged(true);
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, GlobalTransform transform, int mouseX, int mouseY)
    {
        ElixirumScreen.debugRenderer(this, graphics, transform, mouseX, mouseY);
        if (this.visible)
        {
            if (this.centered)
            {
                final var halfWidth = getX() + getWidth() / 2;
                graphics.pose().pushPose();
                graphics.pose().translate(halfWidth, getY(), 0);
                graphics.pose().scale(scale, scale, scale);
                label.renderCentered(graphics, 0, 0, 10, 0xFFFFFFFF);
                graphics.pose().popPose();
            }
            else
            {
                graphics.pose().pushPose();
                graphics.pose().translate(getX(), getY(), 0);
                graphics.pose().scale(scale, scale, scale);
                label.renderLeftAligned(graphics, 0, 0, 10, 0xFFFFFFFF);
                graphics.pose().popPose();
            }
        }
    }

    @Override
    protected void reorganize()
    {
        this.label = MultiLineLabel.create(font, content.copy().setStyle(style), (int) (getWidth() / scale));
        this.setHeight((int) Math.ceil((10 * label.getLineCount() - 1) * scale));
    }
}
