package dev.obscuria.elixirum.client.screen.widgets.pages;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.client.screen.widgets.pages.collection.CollectionPage;
import dev.obscuria.elixirum.client.screen.widgets.pages.discoveries.DiscoveriesPage;
import dev.obscuria.elixirum.client.screen.widgets.pages.recent.RecentlyBrewedPage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public enum PageKind implements StringRepresentable {
    RECENTLY_BREWED(GLFW.GLFW_KEY_1, RecentlyBrewedPage::new),
    COLLECTION(GLFW.GLFW_KEY_2, CollectionPage::new),
    DISCOVERIES(GLFW.GLFW_KEY_3, DiscoveriesPage::new),
    //BOOKS(GLFW.GLFW_KEY_3, BooksPage::new),
    COMPENDIUM(GLFW.GLFW_KEY_4, CompendiumPage::new);

    private final int hotkey;
    private final Supplier<AbstractPage> factory;

    PageKind(int hotkey, Supplier<AbstractPage> factory) {
        this.hotkey = hotkey;
        this.factory = factory;
    }

    public void open() {
        Minecraft.getInstance().setScreen(factory.get());
    }

    public ResourceLocation icon() {
        return ArsElixirum.identifier("textures/gui/page/%s.png".formatted(getSerializedName()));
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
