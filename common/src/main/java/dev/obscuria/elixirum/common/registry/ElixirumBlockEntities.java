package dev.obscuria.elixirum.common.registry;

import dev.obscuria.elixirum.ArsElixirum;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import dev.obscuria.elixirum.common.world.block.entity.PotionShelfEntity;
import dev.obscuria.fragmentum.FragmentumFactory;
import dev.obscuria.fragmentum.registry.DeferredBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

@SuppressWarnings("all")
public interface ElixirumBlockEntities {

    DeferredBlockEntity<GlassCauldronEntity> GLASS_CAULDRON = register("glass_cauldron",
            () -> FragmentumFactory
                    .newBlockEntityType(GlassCauldronEntity::new, ElixirumBlocks.GLASS_CAULDRON.get())
                    .build(null));
    DeferredBlockEntity<PotionShelfEntity> POTION_SHELF = register("potion_shelf",
            () -> FragmentumFactory
                    .newBlockEntityType(PotionShelfEntity::new, ElixirumBlocks.POTION_SHELF.get())
                    .build(null));

    private static <T extends BlockEntity> DeferredBlockEntity<T> register(String name, Supplier<BlockEntityType<T>> supplier) {
        return ElixirumRegistries.REGISTRAR.registerBlockEntity(ArsElixirum.identifier(name), supplier);
    }

    static void init() {}
}
