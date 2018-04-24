package sonar.flux.energy.handlers.tiles;

import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergySource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.AE2Helper;
import sonar.flux.api.energy.FluxEnergyHandler;
import sonar.flux.api.energy.IFluxEnergyHandler;

import javax.annotation.Nonnull;

@FluxEnergyHandler(modid = "appliedenergistics2", priority = 4)
public class AppliedEnergisticsEnergyHandler implements IFluxEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.AE;
	}

	@Override
	public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing dir) {
		return canAddEnergy(tile, dir) || canRemoveEnergy(tile, dir);
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof IAEPowerStorage;
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		return tile instanceof IEnergySource;
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IAEPowerStorage storage = (IAEPowerStorage) tile;
		return add - (long) storage.injectAEPower(add, AE2Helper.getActionable(actionType));
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {
		IEnergySource source = (IEnergySource) tile;
		return (long) source.extractAEPower(remove, AE2Helper.getActionable(actionType), PowerMultiplier.CONFIG);
	}

}
