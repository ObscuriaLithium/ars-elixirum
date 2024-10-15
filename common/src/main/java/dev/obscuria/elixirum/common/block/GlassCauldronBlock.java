package dev.obscuria.elixirum.common.block;

import com.mojang.serialization.MapCodec;
import dev.obscuria.elixirum.common.block.entity.GlassCauldronEntity;
import dev.obscuria.elixirum.registry.ElixirumBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

public final class GlassCauldronBlock extends BaseEntityBlock
{
    public static final MapCodec<GlassCauldronBlock> CODEC = simpleCodec(GlassCauldronBlock::new);
    private static final VoxelShape SHAPE;

    public GlassCauldronBlock()
    {
        super(BlockBehaviour.Properties
                .ofFullCopy(Blocks.BLACK_STAINED_GLASS)
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(2.0F)
                .noOcclusion());
    }

    private GlassCauldronBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new GlassCauldronEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, ElixirumBlockEntityTypes.GLASS_CAULDRON.value(), GlassCauldronEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof GlassCauldronEntity entity)
            for (var interaction : InteractionQueue.values())
                if (interaction.canUse(entity, stack))
                    return interaction.use(entity, level, player, hand, stack);
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected MapCodec<GlassCauldronBlock> codec()
    {
        return CODEC;
    }

    private enum InteractionQueue
    {
        WATER_BUCKET((entity, stack) -> stack.is(Items.WATER_BUCKET), InteractionQueue::useWaterBucket),
        EMPTY_BUCKET((entity, stack) -> stack.is(Items.BUCKET), InteractionQueue::useEmptyBucket),
        GLASS_BOTTLE((entity, stack) -> stack.is(Items.GLASS_BOTTLE), InteractionQueue::useGlassBottle),
        INGREDIENT((entity, stack) -> true, InteractionQueue::useIngredient);

        private final BiPredicate<GlassCauldronEntity, ItemStack> predicate;
        private final Interaction function;

        InteractionQueue(BiPredicate<GlassCauldronEntity, ItemStack> predicate, Interaction function)
        {
            this.predicate = predicate;
            this.function = function;
        }

        public boolean canUse(GlassCauldronEntity entity, ItemStack stack)
        {
            return this.predicate.test(entity, stack);
        }

        public ItemInteractionResult use(GlassCauldronEntity entity, Level level, Player player, InteractionHand hand, ItemStack stack)
        {
            return this.function.use(entity, level, player, hand, stack);
        }

        private static ItemInteractionResult useWaterBucket(GlassCauldronEntity entity, Level level, Player player, InteractionHand hand, ItemStack stack)
        {
            if (entity.isEmpty())
            {
                if (!level.isClientSide)
                {
                    entity.fillWithWater();
                    stack.shrink(1);
                    entity.playSound(SoundEvents.BUCKET_EMPTY, 1f, 1f);
                    final var emptyBucket = Items.BUCKET.getDefaultInstance();
                    if (!player.addItem(emptyBucket))
                        player.drop(emptyBucket, false);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        private static ItemInteractionResult useEmptyBucket(GlassCauldronEntity entity, Level level, Player player, InteractionHand hand, ItemStack stack)
        {
            if (entity.isFilled())
            {
                if (!level.isClientSide)
                {
                    if (!entity.hasElixir())
                    {
                        entity.pickUpWater();
                        stack.shrink(1);
                        entity.playSound(SoundEvents.BUCKET_FILL, 1f, 1f);
                        final var waterBucket = Items.WATER_BUCKET.getDefaultInstance();
                        if (!player.addItem(waterBucket))
                            player.drop(waterBucket, false);
                    }
                    else
                    {
                        entity.flushElixir();
                        entity.playSound(SoundEvents.BUCKET_FILL, 1f, 1f);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        private static ItemInteractionResult useGlassBottle(GlassCauldronEntity entity, Level level, Player player, InteractionHand hand, ItemStack stack)
        {
            if (entity.isFilled() && entity.hasElixir() && entity.isBoil())
            {
                final var elixir = entity.brew(player);
                if (!level.isClientSide && !elixir.isEmpty())
                {
                    stack.shrink(1);
                    if (!player.addItem(elixir))
                        player.drop(elixir, false);
                    level.playSound(null, player, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1f, 1f);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        private static ItemInteractionResult useIngredient(GlassCauldronEntity entity, Level level, Player player, InteractionHand hand, ItemStack stack)
        {
            return entity.isBoil() && entity.addIngredient(stack)
                    ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                    : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @FunctionalInterface
        private interface Interaction
        {
            ItemInteractionResult use(GlassCauldronEntity entity,
                                      Level level, Player player,
                                      InteractionHand hand, ItemStack stack);
        }
    }

    static
    {
        SHAPE = Shapes.join(
                Shapes.join(
                        Shapes.join(
                                box(4, 3, 4, 12, 5, 12),
                                box(1.5, 4, 1.5, 14.5, 6, 14.5),
                                BooleanOp.OR),
                        box(0, 6, 0, 16, 13, 16),
                        BooleanOp.OR),
                Shapes.join(
                        box(1, 7, 1, 15, 13, 15),
                        box(2.5, 5, 2.5, 13.5, 7, 13.5),
                        BooleanOp.OR),
                BooleanOp.ONLY_FIRST);
    }
}
