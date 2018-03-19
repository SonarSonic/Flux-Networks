package sonar.flux.common.tileentity.energy;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;

@Optional.InterfaceList({
	@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux"),
    @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux")
})
public abstract class TileFluxRedstoneFlux extends TileFluxMekanism implements IEnergyReceiver, IEnergyProvider  {

	public TileFluxRedstoneFlux(ConnectionType type) {
		super(type);
	}

    @Override
    @Optional.Method(modid = "redstoneflux")
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return (int) addPhantomEnergyToNetwork(from, maxReceive, EnergyType.RF, ActionType.getTypeForAction(simulate));
	}

    @Override
    @Optional.Method(modid = "redstoneflux")
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return (int) removePhantomEnergyFromNetwork(from, maxExtract, EnergyType.RF, ActionType.getTypeForAction(simulate));
	}
}
