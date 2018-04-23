package sonar.flux.energy.handlers.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.FluxEnergyHandler;
import sonar.flux.api.energy.IFluxEnergyHandler;

@FluxEnergyHandler(modid = "sonarcore", priority = 0)
public class ForgeEnergyHandler implements IFluxEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.FE;
	}
	
	@Override
	public boolean canRenderConnection(TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(CapabilityEnergy.ENERGY, dir);
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
			return storage.canReceive();
		}
		return false;
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		if (canRenderConnection(tile, dir)) {
			IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
			return storage.canExtract();
		}
		return false;
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		return storage.receiveEnergy((int) Math.min(Integer.MAX_VALUE, add), actionType.shouldSimulate());
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
		return storage.extractEnergy((int) Math.min(Integer.MAX_VALUE, remove), actionType.shouldSimulate());
	}

}
