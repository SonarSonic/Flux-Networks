package sonar.fluxnetworks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.*;

/**
 * Defines the block base class for any flux device that can connect to adjacent blocks
 */
public abstract class FluxConnectorBlock extends FluxDeviceBlock implements IWaterLoggable {

    // the indices are equal to that of Direction.values[]
    public static final BooleanProperty[] SIDES_CONNECTED = new BooleanProperty[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

    public FluxConnectorBlock(Properties props) {
        super(props);
        setDefaultState(getStateContainer().getBaseState()
                .with(DOWN, Boolean.FALSE)
                .with(UP, Boolean.FALSE)
                .with(NORTH, Boolean.FALSE)
                .with(SOUTH, Boolean.FALSE)
                .with(WEST, Boolean.FALSE)
                .with(EAST, Boolean.FALSE)
                .with(WATERLOGGED, Boolean.FALSE)
        );
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos,
                                @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        // block changed
        TileFluxDevice tile = (TileFluxDevice) worldIn.getTileEntity(pos);
        if (tile != null && !tile.getFluxWorld().isRemote) {
            Direction dir = FluxUtils.getBlockDirection(pos, fromPos);
            if (dir != null) {
                tile.updateTransfers(dir);
            }
        }
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);
        // tile changed, not always being called, because some mods do not call related methods
        TileFluxDevice tile = (TileFluxDevice) world.getTileEntity(pos);
        if (tile != null && !tile.getFluxWorld().isRemote) {
            Direction dir = FluxUtils.getBlockDirection(pos, neighbor);
            if (dir != null) {
                tile.updateTransfers(dir);
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return getDefaultState().with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
    }

    @Nonnull
    @Override
    public BlockState updatePostPlacement(@Nonnull BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState,
                                          @Nonnull IWorld worldIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(SIDES_CONNECTED);
        builder.add(WATERLOGGED);
    }

    @Nonnull
    @Override
    public FluidState getFluidState(@Nonnull BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }
}
