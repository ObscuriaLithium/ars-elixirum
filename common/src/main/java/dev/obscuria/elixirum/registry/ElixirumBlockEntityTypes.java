package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import dev.obscuria.elixirum.common.block.entity.PotionShelfEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface ElixirumBlockEntityTypes {
    BlockEntityType<GlassCauldronEntity> GLASS_CAULDRON = register("glass_cauldron.json",
            Elixirum.PLATFORM.createBlockEntityType(GlassCauldronEntity::new, ElixirumBlocks.GLASS_CAULDRON.value()));
    BlockEntityType<PotionShelfEntity> POTION_SHELF = register("potion_shelf",
            Elixirum.PLATFORM.createBlockEntityType(PotionShelfEntity::new, ElixirumBlocks.POTION_SHELF.value()));

    @SuppressWarnings("DataFlowIssue")
    private static <T extends BlockEntity> BlockEntityType<T>
    register(final String name, BlockEntityType.Builder<T> builder) {
        final var value = builder.build(null);
        Elixirum.PLATFORM.registerReference(BuiltInRegistries.BLOCK_ENTITY_TYPE, Elixirum.key(name), () -> value);
        return value;
    }

    static void setup() {}
}
