package dev.obscuria.elixirum.common.world.block;

import com.google.common.collect.ImmutableMap;
import dev.obscuria.elixirum.common.world.block.entity.PotionShelfEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class PotionShelfBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final ImmutableMap<Direction, VoxelShape[]> SHAPES;

    public PotionShelfBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotionShelfEntity(pos, state);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var direction = state.getValue(FACING);
        var relative = pos.relative(direction.getOpposite());
        return level.getBlockState(relative).isFaceSturdy(level, relative, direction);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        for (var direction : context.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                var opposite = direction.getOpposite();
                var fluidState = context.getLevel().getFluidState(context.getClickedPos());
                var state = this.defaultBlockState()
                        .setValue(WATERLOGGED, fluidState.is(Fluids.WATER))
                        .setValue(FACING, opposite);
                if (!state.canSurvive(context.getLevel(), context.getClickedPos())) continue;
                return state;
            }
        }
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var shape = Objects.requireNonNull(SHAPES.get(state.getValue(FACING)));
        return computeShape(level.getBlockEntity(pos), shape);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {

        var stack = player.getItemInHand(hand);

        if (level.getBlockEntity(pos) instanceof PotionShelfEntity entity) {
            var local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());

            var slotIndex = switch (state.getValue(FACING)) {
                case NORTH -> getClickedSlot(entity, 1 - local.x);
                case SOUTH -> getClickedSlot(entity, local.x);
                case WEST -> getClickedSlot(entity, local.z);
                default -> getClickedSlot(entity, 1 - local.z);
            };

            return switch (slotIndex) {
                case 1 -> useOnSlot(0, entity, stack, player, hand);
                case 2 -> useOnSlot(1, entity, stack, player, hand);
                case 3 -> useOnSlot(2, entity, stack, player, hand);
                default -> InteractionResult.PASS;
            };
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(
            BlockState state, Level level, BlockPos pos,
            BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof PotionShelfEntity entity) {
            for (var stack : entity.removeAllItems()) {
                Block.popResource(level, pos, stack);
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
    }

    private InteractionResult useOnSlot(
            int slot, PotionShelfEntity entity, ItemStack stack,
            Player player, InteractionHand hand) {

        if (stack.isEmpty()) {
            player.setItemInHand(hand, entity.takeItem(slot));
        } else {
            entity.placeItem(slot, stack);
        }

        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    private int getClickedSlot(PotionShelfEntity entity, double position) {
        if (entity.getItem(1).isEmpty()) {
            if (position <= 0.35) return 1;
            if (position >= 0.65) return 3;
            return 2;
        }

        return 1 + (int) Math.floor(position * 3);
    }

    private VoxelShape computeShape(@Nullable BlockEntity entity, VoxelShape[] shapes) {
        var shape = shapes[0];
        if (entity instanceof PotionShelfEntity shelf) {
            if (!shelf.getItem(0).isEmpty())
                shape = Shapes.or(shape, shapes[1]);
            if (!shelf.getItem(1).isEmpty())
                shape = Shapes.or(shape, shapes[2]);
            if (!shelf.getItem(2).isEmpty())
                shape = Shapes.or(shape, shapes[3]);
        }
        return shape;
    }

    static {
        var north = Shapes.or(
                Block.box(0.0, 8.0, 8.0, 16.0, 10.0, 16.0),
                Block.box(7.0, 3.0, 9.0, 9.0, 8.0, 16.0));
        var north1 = Block.box(10.5, 10.0, 10.0, 15.5, 16.0, 14.0);
        var north2 = Block.box(5.5, 10.0, 10.0, 10.5, 16.0, 14.0);
        var north3 = Block.box(0.5, 10.0, 10.0, 5.5, 16.0, 14.0);
        var south = Shapes.or(
                Block.box(0.0, 8.0, 0.0, 16.0, 10.0, 8.0),
                Block.box(7.0, 3.0, 0.0, 9.0, 8.0, 7.0));
        var south1 = Block.box(0.5, 10.0, 2.0, 5.5, 16.0, 6.0);
        var south2 = Block.box(5.5, 10.0, 2.0, 10.5, 16.0, 6.0);
        var south3 = Block.box(10.5, 10.0, 2.0, 15.5, 16.0, 6.0);
        var west = Shapes.or(
                Block.box(8.0, 8.0, 0.0, 16.0, 10.0, 16.0),
                Block.box(9.0, 3.0, 7.0, 16.0, 8.0, 9.0));
        var west1 = Block.box(10.0, 10.0, 0.5, 14.0, 16.0, 5.5);
        var west2 = Block.box(10.0, 10.0, 5.5, 14.0, 16.0, 10.5);
        var west3 = Block.box(10.0, 10.0, 10.5, 14.0, 16.0, 15.5);
        var east = Shapes.or(
                Block.box(0.0, 8.0, 0.0, 8.0, 10.0, 16.0),
                Block.box(0.0, 3.0, 7.0, 7.0, 8.0, 9.0));
        var east1 = Block.box(2.0, 10.0, 10.5, 6.0, 16.0, 15.5);
        var east2 = Block.box(2.0, 10.0, 5.5, 6.0, 16.0, 10.5);
        var east3 = Block.box(2.0, 10.0, 0.5, 6.0, 16.0, 5.5);

        SHAPES = Util.make(ImmutableMap.<Direction, VoxelShape[]>builder(), shapes -> {
            shapes.put(Direction.NORTH, new VoxelShape[]{north, north1, north2, north3});
            shapes.put(Direction.SOUTH, new VoxelShape[]{south, south1, south2, south3});
            shapes.put(Direction.WEST, new VoxelShape[]{west, west1, west2, west3});
            shapes.put(Direction.EAST, new VoxelShape[]{east, east1, east2, east3});
        }).build();
    }
}
