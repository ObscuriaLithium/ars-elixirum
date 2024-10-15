package dev.obscuria.elixirum.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.obscuria.elixirum.client.screen.ElixirumScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public interface ElixirumKeyMappings
{
    KeyMapping MENU = new KeyMapping("key.elixirum.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.elixirum");

    static void menuPressed(Minecraft minecraft)
    {
        if (minecraft.screen != null) return;
        minecraft.setScreen(new ElixirumScreen());
    }
}
