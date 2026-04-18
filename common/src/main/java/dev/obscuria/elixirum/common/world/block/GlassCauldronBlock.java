package dev.obscuria.elixirum.common.world.block;

import dev.obscuria.elixirum.common.registry.ElixirumBlockEntities;
import dev.obscuria.elixirum.common.world.block.entity.GlassCauldronEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class GlassCauldronBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE;

    public GlassCauldronBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlassCauldronEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ElixirumBlockEntities.GLASS_CAULDRON.get(), GlassCauldronEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof GlassCauldronEntity entity) {
            final var stack = player.getItemInHand(hand);
            for (var interaction : entity.actualInteractions()) {
                if (!interaction.shouldInteract(stack)) continue;
                return interaction.interact(entity, level, stack, player, hand);
            }
        }
        return InteractionResult.PASS;
    }

    static {
        SHAPE = Shapes.join(
                Shapes.join(
                        Shapes.join(
                                box(4.0, 3.0, 4.0, 12.0, 5.0, 12.0),
                                box(1.5, 4.0, 1.5, 14.5, 6.0, 14.5),
                                BooleanOp.OR),
                        box(0.0, 6.0, 0.0, 16.0, 13.0, 16.0),
                        BooleanOp.OR),
                Shapes.join(
                        box(1.0, 7.0, 1.0, 15.0, 13.0, 15.0),
                        box(2.5, 5.0, 2.5, 13.5, 7.0, 13.5),
                        BooleanOp.OR),
                BooleanOp.ONLY_FIRST);
    }
}
