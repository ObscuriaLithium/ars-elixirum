package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.alchemy.properties.AlchemyProperties;
import dev.obscuria.elixirum.network.S2CAlchemyMapMessage;
import dev.obscuria.elixirum.server.ServerAlchemyPropertyMap;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientAlchemy {
    static final ServerAlchemyPropertyMap PROPERTIES_MAP = new ServerAlchemyPropertyMap();
    static final Logger LOG = LoggerFactory.getLogger(Elixirum.DISPLAY_NAME + "/Client");

    public static boolean hasProperties(Item item) {
        return PROPERTIES_MAP.hasProperties(item);
    }

    public static AlchemyProperties getProperties(Item item) {
        return PROPERTIES_MAP.getProperties(item);
    }

    @ApiStatus.Internal
    public static void handle(S2CAlchemyMapMessage message) {
        LOG.info("Loaded {} properties",
                message.packedMap().properties().size());
        PROPERTIES_MAP.unpack(message.packedMap());
    }
}
