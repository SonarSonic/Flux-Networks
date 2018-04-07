package sonar.flux.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.common.block.ConnectedTile;
import sonar.core.common.block.ConnectedTile.PropertySonarFacing;
import sonar.core.helpers.SonarHelper;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.common.tileentity.energy.TileAbstractEnergyConnector;

public abstract class FluxSidedConnection extends FluxConnection {

	FluxSidedConnection() {
		super();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos, neighbor);
		updateTransfers(world, pos, neighbor);
	}

	@Override
	public void observedNeighborChange(IBlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {
		super.observedNeighborChange(observerState, world, observerPos, changedBlock, changedBlockPos);
		updateTransfers(world, observerPos, changedBlockPos);
	}

	public void updateTransfers(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && !tile.getWorld().isRemote && tile instanceof TileAbstractEnergyConnector) {
			TileAbstractEnergyConnector flux = (TileAbstractEnergyConnector) tile;
			flux.onNeighborChange(SonarHelper.getBlockDirection(pos, neighbor));
		}
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		IBlockState currentState = state;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		TileEntity tile = w.getTileEntity(pos);
		if (tile != null && tile instanceof TileFlux) {
			TileFlux flux = (TileFlux) tile;
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