package sonar.flux.common.tileentity.energy;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

public abstract class TileFluxTesla extends TileFluxRedstoneFlux {

	public TileFluxTesla(ConnectionType type) {
		super(type);
	}
	
	@Override
    @Optional.Method(modid = "tesla")
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (getConnectionType().canAddPhantomPower() && capability == TeslaCapabilities.CAPABILITY_CONSUMER){
			return true;
		}
		if (getConnectionType().canRemovePhantomPower() && capability == TeslaCapabilities.CAPABILITY_PRODUCER){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	
	@Override
    @Optional.Method(modid = "tesla")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (getConnectionType().canAddPhantomPower() && capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
			return (T) getConnectionWrapper(facing);
		}
		if (getConnectionType().canRemovePhantomPower() && capability == TeslaCapabilities.CAPABILITY_PRODUCER){
			return (T) getConnectionWrapper(facing);
		}
		return super.getCapability(capability, facing);
	}
}
