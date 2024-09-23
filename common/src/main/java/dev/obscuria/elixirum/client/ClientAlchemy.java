package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.elixir.ElixirRecipe;
import dev.obscuria.elixirum.network.ClientboundDiscoverPacket;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.network.ClientboundProfilePacket;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.util.Lazy;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class ClientAlchemy {
    static final Logger LOG = LoggerFactory.getLogger(Elixirum.DISPLAY_NAME + "/Client");
    static final ClientIngredients itemEssences = new ClientIngredients();
    static final ClientElixirumProfile profile = new ClientElixirumProfile();
    static final List<RecentElixir> recentElixirs = Lists.newArrayList();

    public static ClientIngredients getIngredients() {
        return itemEssences;
    }

    public static ClientElixirumProfile getProfile() {
        return profile;
    }

    public static void addRecentElixir(ItemStack stack, ElixirRecipe recipe) {
        if (recentElixirs.stream().anyMatch(recent -> recent.recipe.equals(recipe))) return;
        recentElixirs.addFirst(new RecentElixir(stack.copy(), recipe));
    }

    @Contract(pure = true)
    public static @Unmodifiable List<RecentElixir> getRecentElixirs() {
        return List.copyOf(recentElixirs);
    }

    @ApiStatus.Internal
    public static void handle(ClientboundItemEssencesPacket packet) {
        LOG.info("Loaded {} item essences", packet.packed().properties().size());
        itemEssences.unpack(packet.packed());
    }

    @ApiStatus.Internal
    public static void handle(ClientboundProfilePacket packet) {
        profile.unpack(packet.content());
    }

    @ApiStatus.Internal
    public static void handle(ClientboundDiscoverPacket packet) {
        profile.handle(packet);
    }

    public record RecentElixir(ItemStack stack, ElixirRecipe recipe, Lazy<Boolean> saved) {

        private RecentElixir(ItemStack stack, ElixirRecipe recipe) {
            this(stack, recipe, Lazy.pure(() -> getProfile().isSaved(recipe)));
        }
    }
}
