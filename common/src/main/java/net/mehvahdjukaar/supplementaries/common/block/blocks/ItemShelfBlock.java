package net.mehvahdjukaar.supplementaries.common.block.blocks;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.mehvahdjukaar.moonlight.api.block.ItemDisplayTile;
import net.mehvahdjukaar.moonlight.api.block.WaterBlock;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.supplementaries.common.block.tiles.ItemShelfBlockTile;
import net.mehvahdjukaar.supplementaries.common.utils.BlockUtil;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ItemShelfBlock extends WaterBlock implements EntityBlock {

    protected static final VoxelShape SHAPE_NORTH = Block.box(0D, 1.0D, 13.0D, 16.0D, 4.0D, 16.0D);
    protected static final VoxelShape SHAPE_SOUTH = Utils.rotateVoxelShape(SHAPE_NORTH, Direction.SOUTH);
    protected static final VoxelShape SHAPE_WEST = Utils.rotateVoxelShape(SHAPE_NORTH, Direction.WEST);
    protected static final VoxelShape SHAPE_EAST = Utils.rotateVoxelShape(SHAPE_NORTH, Direction.EAST);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ItemShelfBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType pathType) {
        return true;
    }

    @PlatformOnly(PlatformOnly.FORGE)
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return CommonConfigs.Blocks.ITEM_SHELF_LADDER.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return !worldIn.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isAir();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (context.getClickedFace() == Direction.UP || context.getClickedFace() == Direction.DOWN)
            return state.setValue(FACING, Direction.NORTH);
        return state.setValue(FACING, context.getClickedFace());
    }

    //called when a neighbor is placed
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return facing == stateIn.getValue(FACING).getOpposite() && !stateIn.canSurvive(worldIn, currentPos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }


    //@Override
    @PlatformOnly(PlatformOnly.FORGE)
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        if (target.getLocation().y() >= pos.getY() + 0.25) {
            if (world.getBlockEntity(pos) instanceof ItemShelfBlockTile tile) {
                ItemStack i = tile.getItem(0);
                if (!i.isEmpty()) return i;
            }
        }
        return super.getCloneItemStack(world, pos, state);

    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (worldIn.getBlockEntity(pos) instanceof ItemDisplayTile tile) {
            return tile.interact(player, handIn);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
        };
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        return worldIn.getBlockEntity(pos) instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ItemShelfBlockTile(pPos, pState);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof ItemShelfBlockTile tile) {
                Containers.dropContents(world, pos, tile);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof Container tile)
            return tile.isEmpty() ? 0 : 15;
        else
            return 0;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);
        BlockUtil.addOptionalOwnership(entity, world, pos);
    }
}