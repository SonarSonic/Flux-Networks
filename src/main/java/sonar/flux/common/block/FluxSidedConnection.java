package sonar.flux.common.block;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import sonar.core.common.block.ConnectedTile;
import sonar.core.common.block.ConnectedTile.PropertySonarFacing;
import sonar.flux.common.tileentity.TileEntityFlux;

public abstract class FluxSidedConnection extends FluxConnection {

    FluxSidedConnection() {
		super();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && !tile.getWorld().isRemote && tile instanceof TileEntityFlux) {
			TileEntityFlux flux = (TileEntityFlux) tile;
            flux.updateNeighbours(true);
		}
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		IBlockState currentState = state;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		TileEntity tile = w.getTileEntity(pos);
		if (tile != null && tile instanceof TileEntityFlux) {
			TileEntityFlux flux = (TileEntityFlux) tile;
			for (PropertySonarFacing face : ConnectedTile.faces) {
				currentState = currentState.withProperty(face, flux.connections.getObjects().get(face.facing.getIndex()));
			}
		}
		return currentState;
	}

	protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECTED, ConnectedTile.NORTH, ConnectedTile.EAST, ConnectedTile.SOUTH, ConnectedTile.WEST, ConnectedTile.DOWN, ConnectedTile.UP);
	}
}