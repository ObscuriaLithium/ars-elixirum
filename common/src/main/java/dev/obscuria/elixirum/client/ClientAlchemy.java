package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.essence.ItemEssences;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import dev.obscuria.elixirum.server.ServerItemEssenceMap;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientAlchemy {
    static final ServerItemEssenceMap PROPERTIES_MAP = new ServerItemEssenceMap();
    static final Logger LOG = LoggerFactory.getLogger(Elixirum.DISPLAY_NAME + "/Client");

    public static boolean hasProperties(Item item) {
        return PROPERTIES_MAP.hasProperties(item);
    }

    public static ItemEssences getProperties(Item item) {
        return PROPERTIES_MAP.getProperties(item);
    }

    @ApiStatus.Internal
    public static void handle(ClientboundItemEssencesPacket packet) {
        LOG.info("Loaded {} item essences", packet.map().properties().size());
        PROPERTIES_MAP.unpack(packet.map());
    }
}
