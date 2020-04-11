package sonar.fluxnetworks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorldReader;
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
        this.setDefaultState(getDefaultState().with(DOWN, true).with(UP, true).with(NORTH, true).with(SOUTH, true).with(EAST, true).with(WEST, true)); //inventory
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);
        TileFluxConnector tile = (TileFluxConnector) world.getTileEntity(pos);
        if(!tile.getWorld().isRemote) {
            tile.updateTransfers(FluxUtils.getBlockDirection(pos, neighbor));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getConnectedState(getDefaultState(), context.getWorld(), context.getPos());
    }

    public static BlockState getConnectedState(BlockState state, World world, BlockPos pos){
        TileFluxCore tile = (TileFluxCore) world.getTileEntity(pos);
        for(int i = 0 ; i < Direction.values().length; i++){
            state = state.with(CONNECTIONS[i], tile != null && tile.connections[i] == 1);
        }
        return state;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTIONS);
    }
}
