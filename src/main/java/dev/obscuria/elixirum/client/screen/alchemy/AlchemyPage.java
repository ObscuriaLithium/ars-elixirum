package dev.obscuria.elixirum.client.screen.alchemy;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.screen.alchemy.pages.collection.CollectionScreen;
import dev.obscuria.elixirum.client.screen.alchemy.pages.compendium.CompendiumScreen;
import dev.obscuria.elixirum.client.screen.alchemy.pages.discoveries.DiscoveriesScreen;
import dev.obscuria.elixirum.client.screen.alchemy.pages.recent.RecentlyBrewedScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public enum AlchemyPage implements StringRepresentable {
    RECENTLY_BREWED(GLFW.GLFW_KEY_1, RecentlyBrewedScreen::new),
    COLLECTION(GLFW.GLFW_KEY_2, CollectionScreen::new),
    DISCOVERIES(GLFW.GLFW_KEY_3, DiscoveriesScreen::new),
    //BOOKS(GLFW.GLFW_KEY_3, BooksPage::new),
    COMPENDIUM(GLFW.GLFW_KEY_4, CompendiumScreen::new);

    private final int hotkey;
    private final Supplier<AlchemyScreen> factory;

    AlchemyPage(int hotkey, Supplier<AlchemyScreen> factory) {
        this.hotkey = hotkey;
        this.factory = factory;
    }

    public void open() {
        Minecraft.getInstance().setScreen(factory.get());
    }

    public Component displayName() {
        return Component.translatable("ui.elixirum.page." + getSerializedName());
    }

    public ResourceLocation icon() {
        return ArsElixirum.identifier("textures/gui/page/%s.png".formatted(getSerializedName()));
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}