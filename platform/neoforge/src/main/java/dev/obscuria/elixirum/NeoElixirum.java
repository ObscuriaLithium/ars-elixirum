package dev.obscuria.elixirum;

import dev.obscuria.elixirum.server.ServerAlchemy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(Elixirum.MODID)
public class NeoElixirum
{
    public NeoElixirum(IEventBus eventBus)
    {
        Elixirum.init();
        NeoForge.EVENT_BUS.register(Events.class);
    }

    public static final class Events
    {
        @SubscribeEvent
        private static void onServerStarted(ServerStartedEvent event)
        {
            ServerAlchemy.whenServerStarted(event.getServer());
        }

        @SubscribeEvent
        private static void onServerStopped(ServerStoppedEvent event)
        {
            ServerAlchemy.whenServerStopped(event.getServer());
        }
    }
}