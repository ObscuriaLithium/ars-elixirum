package dev.obscuria.elixirum.forge;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.registry.ElixirumAttributes;
import dev.obscuria.elixirum.forge.client.ForgeArsElixirumClient;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ArsElixirum.MODID)
public class ForgeArsElixirum {

    public ForgeArsElixirum() {
        ArsElixirum.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ForgeArsElixirumClient::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeArsElixirum::registerCustomAttributes);
    }

    private static void registerCustomAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ElixirumAttributes.POTION_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.POTION_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.CORPUS_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.CORPUS_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.ANIMA_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.ANIMA_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.VENENUM_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.VENENUM_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.MEDELA_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.MEDELA_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.CRESCERE_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.CRESCERE_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.MUTATIO_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.MUTATIO_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.FORTUNA_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.FORTUNA_RESISTANCE);
        event.add(EntityType.PLAYER, ElixirumAttributes.TENEBRAE_MASTERY);
        event.add(EntityType.PLAYER, ElixirumAttributes.TENEBRAE_RESISTANCE);
    }
}