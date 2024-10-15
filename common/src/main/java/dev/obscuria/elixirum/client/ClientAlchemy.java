package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.client.screen.section.collection.RootCollection;
import dev.obscuria.elixirum.client.screen.section.recent.RootRecent;
import dev.obscuria.elixirum.network.ClientboundDiscoverPayload;
import dev.obscuria.elixirum.network.ClientboundIngredientsPayload;
import dev.obscuria.elixirum.network.ClientboundProfilePayload;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientAlchemy {
    private static final Logger LOG = LoggerFactory.getLogger(Elixirum.DISPLAY_NAME + "/Client");
    private static final ClientIngredients ingredients = new ClientIngredients();
    private static final ClientElixirumProfile profile = new ClientElixirumProfile();
    private static final SessionCache cache = new SessionCache();

    public static void clearCache() {
        ClientAlchemy.getCache().clear();
        RootRecent.reset();
        RootCollection.reset();
    }

    public static SessionCache getCache() {
        return cache;
    }

    public static ClientIngredients getIngredients() {
        return ingredients;
    }

    public static ClientElixirumProfile getProfile() {
        return profile;
    }

    @ApiStatus.Internal
    public static void handle(ClientboundIngredientsPayload packet) {
        LOG.info("Loaded {} ingredients", packet.packed().properties().size());
        ingredients.unpack(packet.packed());
    }

    @ApiStatus.Internal
    public static void handle(ClientboundProfilePayload packet) {
        profile.handle(packet);
    }

    @ApiStatus.Internal
    public static void handle(ClientboundDiscoverPayload packet) {
        profile.handle(packet);
    }
}
