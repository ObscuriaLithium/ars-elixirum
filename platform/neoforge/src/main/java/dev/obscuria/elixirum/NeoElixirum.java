package dev.obscuria.elixirum;

import dev.obscuria.elixirum.server.commands.EssenceCommand;
import dev.obscuria.elixirum.server.commands.RegenerateCommand;
import dev.obscuria.elixirum.common.alchemy.essence.Essence;
import dev.obscuria.elixirum.common.alchemy.ingredient.IngredientPreset;
import dev.obscuria.elixirum.network.ClientNetworkHandler;
import dev.obscuria.elixirum.network.NeoClientboundItemEssencesPayload;
import dev.obscuria.elixirum.platform.NeoPlatform;
import dev.obscuria.elixirum.registry.ElixirumRegistries;
import dev.obscuria.elixirum.server.ServerAlchemy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Elixirum.MODID)
public class NeoElixirum {

    public NeoElixirum(IEventBus bus) {
        Elixirum.initRegistries();
        Elixirum.init();

        NeoForge.EVENT_BUS.register(NeoEvents.class);
        bus.register(ModEvents.class);

        if (Elixirum.PLATFORM.isClient())
            NeoElixirumClient.init();
    }

    public static class ModEvents {

        @SubscribeEvent
        private static void onNewDataRegistry(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(ElixirumRegistries.ESSENCE, Essence.DIRECT_CODEC, Essence.DIRECT_CODEC);
            event.dataPackRegistry(ElixirumRegistries.INGREDIENT_PRESET, IngredientPreset.DIRECT_CODEC);
        }

        @SubscribeEvent
        public static void onRegister(final RegisterEvent event) {
            NeoPlatform.registers.removeIf(register -> {
                if (!event.getRegistryKey().equals(register.getRegistryKey())) return false;
                register.register();
                return true;
            });
        }

        @SubscribeEvent
        public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playBidirectional(
                    NeoClientboundItemEssencesPayload.TYPE,
                    NeoClientboundItemEssencesPayload.STREAM_CODEC,
                    (payload, context) -> ClientNetworkHandler.handle(payload.packet(), context.player())
            );
        }
    }

    public static class NeoEvents {

        @SubscribeEvent
        private static void onRegisterCommands(RegisterCommandsEvent event) {
            EssenceCommand.register(event.getDispatcher(), event.getBuildContext());
            RegenerateCommand.register(event.getDispatcher(), event.getBuildContext());
        }

        @SubscribeEvent
        private static void onServerStarted(ServerStartedEvent event) {
            ServerAlchemy.whenServerStarted(event.getServer());
        }

        @SubscribeEvent
        private static void onServerStopped(ServerStoppedEvent event) {
            ServerAlchemy.whenServerStopped(event.getServer());
        }
    }
}