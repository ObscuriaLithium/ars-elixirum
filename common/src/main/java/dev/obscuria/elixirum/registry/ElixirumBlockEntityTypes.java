package dev.obscuria.elixirum.registry;

import dev.obscuria.core.api.Deferred;
import dev.obscuria.core.api.v1.common.ObscureFactory;
import dev.obscuria.core.api.v1.common.ObscureRegistry;
import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import dev.obscuria.elixirum.common.block.entity.PotionShelfEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface ElixirumBlockEntityTypes
{
    Deferred<BlockEntityType<?>, BlockEntityType<GlassCauldronEntity>> GLASS_CAULDRON = register("glass_cauldron.json",
            () -> ObscureFactory.createBlockEntityType(GlassCauldronEntity::new, ElixirumBlocks.GLASS_CAULDRON.value()));
    Deferred<BlockEntityType<?>, BlockEntityType<PotionShelfEntity>> POTION_SHELF = register("potion_shelf",
            () -> ObscureFactory.createBlockEntityType(PotionShelfEntity::new, ElixirumBlocks.POTION_SHELF.value()));

    @SuppressWarnings("DataFlowIssue")
    private static <T extends BlockEntity> Deferred<BlockEntityType<?>, BlockEntityType<T>>
    register(final String name, Supplier<BlockEntityType.Builder<T>> builder)
    {
        return ObscureRegistry.register(
                Elixirum.MODID,
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                Elixirum.key(name),
                () -> builder.get().build(null));
    }

    static void init() {}
}
