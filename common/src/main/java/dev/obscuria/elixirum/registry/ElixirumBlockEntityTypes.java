package dev.obscuria.elixirum.registry;

import dev.obscuria.elixirum.Elixirum;
import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface ElixirumBlockEntityTypes {
    LazyRegister<BlockEntityType<?>> SOURCE = LazyRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Elixirum.MODID);

    LazyValue<BlockEntityType<?>, BlockEntityType<GlassCauldronEntity>> GLASS_CAULDRON = simple("glass_cauldron",
            () -> Elixirum.PLATFORM.createBlockEntityType(GlassCauldronEntity::new, ElixirumBlocks.GLASS_CAULDRON.value()));

    @SuppressWarnings("DataFlowIssue")
    private static <TValue extends BlockEntity> LazyValue<BlockEntityType<?>, BlockEntityType<TValue>>
    simple(final String name, Supplier<BlockEntityType.Builder<TValue>> supplier) {
        return SOURCE.register(name, () -> supplier.get().build(null));
    }
}
