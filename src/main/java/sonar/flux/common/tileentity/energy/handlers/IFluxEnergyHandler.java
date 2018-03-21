package sonar.flux.common.tileentity.energy.handlers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

public interface IFluxEnergyHandler {
	
	public enum TransferType{
		ADD, REMOVE;
	}
	
	public boolean canRenderConnection(TileEntity tile, EnumFacing direction);	
	
	public boolean canTransferEnergy(TileEntity tile, EnumFacing direction, TransferType type);
	
	public long transferEnergy(long toTransfer, TileEntity tile, EnumFacing direction, TransferType transferType, ActionType actionType);
	
}
