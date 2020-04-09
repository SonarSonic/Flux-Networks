package sonar.fluxnetworks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.common.tileentity.TileFluxConnector;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.state.properties.BlockStateProperties.*;

public abstract class FluxConnectorBlock extends FluxNetworkBlock {

    public static final BooleanProperty[] CONNECTIONS = new BooleanProperty[]{DOWN, UP, NORTH, SOUTH, EAST, WEST};

    public FluxConnectorBlock(Properties props) {
        super(props);
    }

    @Override
    public void observedNeighborChange(BlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {
        super.observedNeighborChange(observerState, world, observerPos, changedBlock, changedBlockPos);
        TileFluxConnector tile = (TileFluxConnector) world.getTileEntity(observerPos);
        if(!tile.getWorld().isRemote) {
            tile.updateTransfers(FluxUtils.getBlockDirection(observerPos, changedBlockPos));
        }
    }

    /*
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getConnectedState(getDefaultState(), context.getWorld(), context.getPos());
    }
     */

    public static BlockState getConnectedState(BlockState state, World world, BlockPos pos){
        TileFluxCore tile = (TileFluxCore) world.getTileEntity(pos);
        for(int i = 0 ; i < Direction.values().length; i++){
            state = state.with(CONNECTIONS[i], tile.connections[i] == 1);
        }
        return state;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTIONS);
    }
}
