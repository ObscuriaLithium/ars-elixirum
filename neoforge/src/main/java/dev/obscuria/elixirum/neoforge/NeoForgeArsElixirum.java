package dev.obscuria.elixirum.neoforge;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.registry.ElixirumAttributes;
import dev.obscuria.elixirum.neoforge.client.NeoForgeArsElixirumClient;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

@Mod(ArsElixirum.MODID)
public final class NeoForgeArsElixirum {

    public NeoForgeArsElixirum(IEventBus modBus) {
        ArsElixirum.init();
        modBus.addListener(NeoForgeArsElixirum::registerAttributes);
        if (FMLEnvironment.dist == Dist.CLIENT) NeoForgeArsElixirumClient.init(modBus);
    }

    private static void registerAttributes(EntityAttributeModificationEvent event) {
        for (var attribute : java.util.List.of(
                ElixirumAttributes.POTION_MASTERY, ElixirumAttributes.POTION_RESISTANCE,
                ElixirumAttributes.CORPUS_MASTERY, ElixirumAttributes.CORPUS_RESISTANCE,
                ElixirumAttributes.ANIMA_MASTERY, ElixirumAttributes.ANIMA_RESISTANCE,
                ElixirumAttributes.VENENUM_MASTERY, ElixirumAttributes.VENENUM_RESISTANCE,
                ElixirumAttributes.MEDELA_MASTERY, ElixirumAttributes.MEDELA_RESISTANCE,
                ElixirumAttributes.CRESCERE_MASTERY, ElixirumAttributes.CRESCERE_RESISTANCE,
                ElixirumAttributes.MUTATIO_MASTERY, ElixirumAttributes.MUTATIO_RESISTANCE,
                ElixirumAttributes.FORTUNA_MASTERY, ElixirumAttributes.FORTUNA_RESISTANCE,
                ElixirumAttributes.TENEBRAE_MASTERY, ElixirumAttributes.TENEBRAE_RESISTANCE)) {
            event.add(EntityType.PLAYER, attribute.holder());
        }
    }
}
