package sonar.flux.common.tileentity.energy;

import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

@Optional.InterfaceList({ 
	@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyOutputter", modid = "mekanism"), 
	@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "mekanism")
})
public abstract class TileFluxMekanism extends TileFluxIC2 implements IStrictEnergyAcceptor, IStrictEnergyOutputter {

	public TileFluxMekanism(ConnectionType type) {
		super(type);
	}

	@Override
	@Optional.Method(modid = "mekanism")
	public double acceptEnergy(EnumFacing side, double maxReceive, boolean simulate) {
		return addPhantomEnergyToNetwork(side, Math.max((long) Math.floor(maxReceive), Long.MAX_VALUE), EnergyType.MJ, ActionType.getTypeForAction(simulate));
	}

    @Override
	@Optional.Method(modid = "mekanism")
    public double pullEnergy(EnumFacing side, double maxExtract, boolean simulate) {
		return removePhantomEnergyFromNetwork(side, Math.max((long) Math.floor(maxExtract), Long.MAX_VALUE), EnergyType.MJ, ActionType.getTypeForAction(simulate));
    }

	@Override
	@Optional.Method(modid = "mekanism")
	public boolean canReceiveEnergy(EnumFacing enumFacing) {
		return getConnectionType().canAddPhantomPower();
	}

	@Override
	@Optional.Method(modid = "mekanism")
    public boolean canOutputEnergy(EnumFacing dir) {
		return getConnectionType().canRemovePhantomPower();
	}

}