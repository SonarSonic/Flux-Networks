package sonar.flux.connection.transfer.handlers;

import javax.annotation.Nullable;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.tiles.IFluxConnection;
import sonar.flux.api.tiles.IFluxPoint;

@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla")
})
public class FluxConnectionWrapper implements IEnergyStorage, ITeslaConsumer, ITeslaProducer {

	public final @Nullable EnumFacing side;
	public final IFluxConnection flux;

	public FluxConnectionWrapper(@Nullable EnumFacing side, IFluxConnection flux) {
		this.side = side;
		this.flux = flux;
	}

	//// FORGE ENERGY \\\\

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return (int) flux.addPhantomEnergyToNetwork(side, Math.min(maxReceive, Integer.MAX_VALUE), EnergyType.FE, ActionType.getTypeForAction(simulate));
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return (int) flux.removePhantomEnergyFromNetwork(side, Math.min(maxExtract, Integer.MAX_VALUE), EnergyType.FE, ActionType.getTypeForAction(simulate));
	}

	@Override
	public int getEnergyStored() {
		return flux instanceof IFluxPoint ? Integer.MAX_VALUE : 0;
	}

	@Override
	public int getMaxEnergyStored() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canExtract() {
		return flux.getConnectionType().canRemovePhantomPower();
	}

	@Override
	public boolean canReceive() {
		return flux.getConnectionType().canAddPhantomPower(); //opposites as this is if it send throughout the network
	}

	//// TESLA \\\\
	
	@Override
    @Optional.Method(modid = "tesla")
	public long givePower(long power, boolean simulated) {
        return flux.addPhantomEnergyToNetwork(side, Math.min(power, Long.MAX_VALUE), EnergyType.TESLA, ActionType.getTypeForAction(simulated));
	}

	@Override
    @Optional.Method(modid = "tesla")
	public long takePower(long power, boolean simulated) {
        return flux.removePhantomEnergyFromNetwork(side, Math.min(power, Long.MAX_VALUE), EnergyType.TESLA, ActionType.getTypeForAction(simulated));
	}
}
