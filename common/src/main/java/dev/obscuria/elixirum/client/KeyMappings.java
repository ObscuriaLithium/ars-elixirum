package dev.obscuria.elixirum.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.obscuria.elixirum.client.screen.alchemy.AlchemyScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public interface KeyMappings {

    String CATEGORY = "keyCategory.elixirum";

    KeyMapping COLLECTION = new KeyMapping("key.elixirum.collection", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);

    static void collectionPressed() {
        if (Minecraft.getInstance().screen != null) return;
        AlchemyScreen.lastPage.open();
    }
}
