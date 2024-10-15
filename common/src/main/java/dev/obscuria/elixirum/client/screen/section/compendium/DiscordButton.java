package dev.obscuria.elixirum.client.screen.section.compendium;

import dev.obscuria.elixirum.client.screen.tool.ClickAction;
import dev.obscuria.elixirum.client.screen.widget.Button;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

final class DiscordButton extends Button
{
    public DiscordButton()
    {
        super(Component.literal("Obscuria Collection Discord"));
        this.setClickAction(ClickAction.<DiscordButton>left(button -> {
            Util.getPlatform().openUri("https://discord.gg/jSHHJSUWdY");
            return true;
        }));
    }
}
