package dev.obscuria.elixirum.common.block;

import com.mojang.serialization.MapCodec;
import dev.obscuria.elixirum.common.block.entity.PotionShelfEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PotionShelfBlock extends BaseEntityBlock
{
    public static final MapCodec<PotionShelfBlock> CODEC = simpleCodec(PotionShelfBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final Map<Direction, VoxelShape[]> SHAPES;

    public PotionShelfBlock()
    {
        this(Properties.ofFullCopy(Blocks.SPRUCE_PLANKS).noOcclusion());
    }

    private PotionShelfBlock(Properties properties)
    {
        super(properties);
        this.stateDefinition.any().setValue(FACING, Direction.NORTH);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        var direction = state.getValue(FACING);
        var relative = pos.relative(direction.getOpposite());
        return level.getBlockState(relative).isFaceSturdy(level, relative, direction);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        final var level = context.getLevel();
        final var pos = context.getClickedPos();
        final var directions = context.getNearestLookingDirections();

        for (var direction : directions)
        {
            if (direction.getAxis().isHorizontal())
            {
                final var opposite = direction.getOpposite();
                final var state = defaultBlockState().setValue(FACING, opposite);
                if (state.canSurvive(level, pos))
                    return state;
            }
        }

        return null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new PotionShelfEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return this.computeShape(level.getBlockEntity(pos), SHAPES.get(state.getValue(FACING)));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec()
    {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof PotionShelfEntity entity)
        {
            final var vec = hitResult.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            final int index = switch (state.getValue(FACING))
            {
                case NORTH -> this.getClickedSlot(entity, 1 - vec.x);
                case SOUTH -> this.getClickedSlot(entity, vec.x);
                case WEST -> this.getClickedSlot(entity, vec.z);
                default -> this.getClickedSlot(entity, 1 - vec.z);
            };
            return switch (index)
            {
                case 1 -> this.useOnSlot(stack, player, hand, entity::takeFirstStack, entity::putFirstStack);
                case 2 -> this.useOnSlot(stack, player, hand, entity::takeSecondStack, entity::putSecondStack);
                case 3 -> this.useOnSlot(stack, player, hand, entity::takeThirdStack, entity::putThirdStack);
                default -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            };
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!state.is(newState.getBlock()))
        {
            if (level.getBlockEntity(pos) instanceof PotionShelfEntity entity)
            {
                final var first = entity.getFirstStack();
                final var second = entity.getSecondStack();
                final var third = entity.getThirdStack();
                if (!first.isEmpty()) popResource(level, pos, first);
                if (!second.isEmpty()) popResource(level, pos, second);
                if (!third.isEmpty()) popResource(level, pos, third);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private ItemInteractionResult useOnSlot(ItemStack stack, Player player,
                                            InteractionHand hand,
                                            Supplier<ItemStack> takeFunc,
                                            Function<ItemStack, Boolean> putFunc)
    {
        if (stack.isEmpty()) player.setItemInHand(hand, takeFunc.get());
        else putFunc.apply(stack);
        return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
    }

    private int getClickedSlot(PotionShelfEntity entity, double position)
    {
        if (entity.getSecondStack().isEmpty())
            return position <= 0.35f ? 1 : position >= 0.65 ? 3 : 2;
        return 1 + (int) Math.floor(position * 3);
    }

    private VoxelShape computeShape(@Nullable BlockEntity entity, VoxelShape[] shapes)
    {
        var shape = shapes[0];
        if (entity instanceof PotionShelfEntity shelf)
        {
            if (!shelf.getFirstStack().isEmpty()) shape = Shapes.join(shape, shapes[1], BooleanOp.OR);
            if (!shelf.getSecondStack().isEmpty()) shape = Shapes.join(shape, shapes[2], BooleanOp.OR);
            if (!shelf.getThirdStack().isEmpty()) shape = Shapes.join(shape, shapes[3], BooleanOp.OR);
        }
        return shape;
    }

    static
    {
        final var north = Shapes.join(
                box(0, 8, 8, 16, 10, 16),
                box(7, 3, 9, 9, 8, 16),
                BooleanOp.OR);
        final var north1 = box(10.5, 10, 10, 15.5, 16, 14);
        final var north2 = box(5.5, 10, 10, 10.5, 16, 14);
        final var north3 = box(0.5, 10, 10, 5.5, 16, 14);

        final var south = Shapes.join(
                box(0, 8, 0, 16, 10, 8),
                box(7, 3, 0, 9, 8, 7),
                BooleanOp.OR);
        final var south1 = box(0.5, 10, 2, 5.5, 16, 6);
        final var south2 = box(5.5, 10, 2, 10.5, 16, 6);
        final var south3 = box(10.5, 10, 2, 15.5, 16, 6);

        final var west = Shapes.join(
                box(8, 8, 0, 16, 10, 16),
                box(9, 3, 7, 16, 8, 9),
                BooleanOp.OR);
        final var west1 = box(10, 10, 0.5, 14, 16, 5.5);
        final var west2 = box(10, 10, 5.5, 14, 16, 10.5);
        final var west3 = box(10, 10, 10.5, 14, 16, 15.5);

        final var east = Shapes.join(
                box(0, 8, 0, 8, 10, 16),
                box(0, 3, 7, 7, 8, 9),
                BooleanOp.OR);
        final var east1 = box(2, 10, 10.5, 6, 16, 15.5);
        final var east2 = box(2, 10, 5.5, 6, 16, 10.5);
        final var east3 = box(2, 10, 0.5, 6, 16, 5.5);

        SHAPES = Map.of(
                Direction.NORTH, new VoxelShape[]{north, north1, north2, north3},
                Direction.SOUTH, new VoxelShape[]{south, south1, south2, south3},
                Direction.WEST, new VoxelShape[]{west, west1, west2, west3},
                Direction.EAST, new VoxelShape[]{east, east1, east2, east3});
    }
}
