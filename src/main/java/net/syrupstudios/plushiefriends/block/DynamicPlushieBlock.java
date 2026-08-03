package net.syrupstudios.plushiefriends.block;

//? if >=1.21
/*import com.mojang.serialization.MapCodec;*/
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.syrupstudios.plushiefriends.block.entity.DynamicPlushieBlockEntity;
import net.syrupstudios.plushiefriends.PlushieFriends;
import org.jetbrains.annotations.Nullable;

public class DynamicPlushieBlock extends BaseEntityBlock {
    //? if >=1.21 {
    /*public static final MapCodec<DynamicPlushieBlock> CODEC = simpleCodec(DynamicPlushieBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    *///?}
    private static final int ROTATION_COUNT = RotationSegment.getMaxSegmentIndex() + 1;
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape HEAD = Block.box(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
    private static final VoxelShape TORSO = Block.box(6.0, 0.0, 7.0, 10.0, 6.0, 9.0);

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            HEAD, TORSO,
            Block.box(5.0, 0.0, 2.0, 11.0, 2.0, 7.0),
            Block.box(3.5, 1.0, 6.5, 12.5, 5.0, 9.0)
    );

    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            HEAD, TORSO,
            Block.box(5.0, 0.0, 9.0, 11.0, 2.0, 14.0),
            Block.box(3.5, 1.0, 7.0, 12.5, 5.0, 9.5)
    );

    private static final VoxelShape SHAPE_EAST = Shapes.or(
            HEAD,
            Block.box(7.0, 0.0, 6.0, 9.0, 6.0, 10.0),
            Block.box(9.0, 0.0, 5.0, 14.0, 2.0, 11.0),
            Block.box(7.0, 1.0, 3.5, 9.5, 5.0, 12.5)
    );

    private static final VoxelShape SHAPE_WEST = Shapes.or(
            HEAD,
            Block.box(7.0, 0.0, 6.0, 9.0, 6.0, 10.0),
            Block.box(2.0, 0.0, 5.0, 7.0, 2.0, 11.0),
            Block.box(6.5, 1.0, 3.5, 9.0, 5.0, 12.5)
    );

    public DynamicPlushieBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ROTATION, 0)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int rotation = getRotation(state);
        return switch ((rotation + 2) / 4 % 4) {
            case 1 -> SHAPE_EAST;
            case 2 -> SHAPE_SOUTH;
            case 3 -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()))
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state
                .setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), ROTATION_COUNT))
                .setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state
                .setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), ROTATION_COUNT))
                .setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    public static int getRotation(BlockState state) {
        int rotation = state.hasProperty(ROTATION) ? state.getValue(ROTATION) : 0;
        if (rotation == 0 && state.hasProperty(FACING)) {
            return RotationSegment.convertToSegment(state.getValue(FACING).getOpposite());
        }
        return rotation;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DynamicPlushieBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return createTickerHelper(type, PlushieFriends.PLUSHIE_BLOCK_ENTITY, DynamicPlushieBlockEntity::serverTick);
        }
        return null;
    }
}
