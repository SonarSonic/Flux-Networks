package sonar.flux.common.tileentity.energy;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public abstract class TileFluxForgeEnergy extends TileAbstractEnergyConnector {

	public TileFluxForgeEnergy(ConnectionType type) {
		super(type);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return (T) getConnectionWrapper(facing);
		}
		return super.getCapability(capability, facing);
	}
}
