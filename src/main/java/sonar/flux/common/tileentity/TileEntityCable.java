package sonar.flux.common.tileentity;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import sonar.core.api.SonarAPI;
import sonar.core.common.block.ConnectedTile;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.flux.api.IFlux;

//to be fully implemented later
public class TileEntityCable extends TileEntitySonar implements IEnergyReceiver, IEnergyProvider {

	public void update() {
		super.update();
	}

	public void updateConnections() {
		boolean[] sides = new boolean[6];
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos pos = this.pos.offset(face);
			TileEntity tile = getWorld().getTileEntity(pos);
			if (tile != null && !(tile instanceof IFlux)) {
				if (tile instanceof TileEntityCable || tile instanceof IEnergyConnection) {
					sides[face.getIndex()] = true;
					continue;
				}
				if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, face) != null) {
					sides[face.getIndex()] = true;
				}

			} else {
				sides[face.getIndex()] = false;
			}
		}
		this.world.setBlockState(pos, world.getBlockState(pos).withProperty(ConnectedTile.DOWN, sides[0]).withProperty(ConnectedTile.UP, sides[1]).withProperty(ConnectedTile.NORTH, sides[2]).withProperty(ConnectedTile.SOUTH, sides[3]).withProperty(ConnectedTile.WEST, sides[4]).withProperty(ConnectedTile.EAST, sides[5]));
	}

	public void validate() {
		super.validate();
		//add to cable network
	}

	public void invalidate() {
		super.invalidate();
		//remove from cable network
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		//get stored from cached network values
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		//get max stored from cached network values
		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		//always true for now
		return true;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		//extract energy from the network
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		//add energy to the network
		return 0;
	}

}
