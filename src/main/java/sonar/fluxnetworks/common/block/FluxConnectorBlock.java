package sonar.fluxnetworks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;

import static net.minecraft.state.properties.BlockStateProperties.*;

/**
 * Defines the block base class for any flux device that can connect to adjacent blocks
 */
public abstract class FluxConnectorBlock extends FluxDeviceBlock {

    // the indices are equal to that of Direction.values[]
    public static final BooleanProperty[] SIDES_CONNECTED = new BooleanProperty[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};

    public FluxConnectorBlock(Properties props) {
        super(props);
        setDefaultState(getDefaultState()
                .with(DOWN, false)
                .with(UP, false)
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(EAST, false)
        );
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos,
                                @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        // block changed
        TileFluxDevice tile = (TileFluxDevice) worldIn.getTileEntity(pos);
        if (tile != null && !tile.getFluxWorld().isRemote) {
            tile.updateTransfers(FluxUtils.getBlockDirection(pos, fromPos));
        }
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);
        // tile changed, not always being called, because some mods do not call related methods
        TileFluxDevice tile = (TileFluxDevice) world.getTileEntity(pos);
        if (tile != null && !tile.getFluxWorld().isRemote) {
            tile.updateTransfers(FluxUtils.getBlockDirection(pos, neighbor));
        }
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SIDES_CONNECTED);
    }
}
