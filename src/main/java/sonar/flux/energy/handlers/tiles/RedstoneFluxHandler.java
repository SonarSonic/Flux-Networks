package sonar.flux.energy.handlers.tiles;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.asm.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.FluxEnergyHandler;
import sonar.flux.api.energy.IFluxEnergyHandler;

@FluxEnergyHandler(modid = "redstoneflux", priority = 3)
public class RedstoneFluxHandler implements IFluxEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.RF;
	}

	@Override
	public boolean canRenderConnection(TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergyConnection;
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			return tile instanceof IEnergyReceiver;
		}
		return false;
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			return tile instanceof IEnergyProvider;
		}
		return false;
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IEnergyReceiver receiver = (IEnergyReceiver) tile;
		return receiver.receiveEnergy(dir, (int) Math.min(Integer.MAX_VALUE, add), actionType.shouldSimulate());
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {		
		IEnergyProvider receiver = (IEnergyProvider) tile;
		return receiver.extractEnergy(dir, (int) Math.min(Integer.MAX_VALUE, remove), actionType.shouldSimulate());
	}

}
