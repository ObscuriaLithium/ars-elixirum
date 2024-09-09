package dev.obscuria.elixirum.client;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.network.ClientboundItemEssencesPacket;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientAlchemy {
    static final Logger LOG = LoggerFactory.getLogger(Elixirum.DISPLAY_NAME + "/Client");
    static final ClientItemEssences itemEssences = new ClientItemEssences();

    public static ClientItemEssences getItemEssences() {
        return itemEssences;
    }

    @ApiStatus.Internal
    public static void handle(ClientboundItemEssencesPacket packet) {
        LOG.info("Loaded {} item essences", packet.packed().holders().size());
        itemEssences.unpack(packet.packed());
    }
}
