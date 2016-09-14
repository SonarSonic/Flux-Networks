package sonar.flux.common.tileentity;

import cofh.api.energy.IEnergyProvider;
import mekanism.api.energy.ICableOutputter;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.SonarLoader;
import sonar.core.utils.IGuiTile;
import sonar.flux.client.GuiFluxPoint;
import sonar.flux.common.ContainerFlux;

@Optional.InterfaceList({ @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla"), @Optional.Interface(iface = "mekanism.api.energy.ICableOutputter", modid = "Mekanism") })
public class TileEntityPoint extends TileEntityFlux implements IGuiTile, IEnergyProvider, ITeslaProducer, ICableOutputter {

	public TileEntityPoint() {
		super(ConnectionType.POINT);
		customName.setDefault("Flux Point");
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerFlux(player, this, false);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiFluxPoint(player, this);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (maxExtract == 0) {
			return 0;
		}
		return (int) (this.getNetwork().extractEnergy(Math.min(maxExtract, getTransferLimit()), simulate ? ActionType.SIMULATE : ActionType.PERFORM));
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_PRODUCER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
				return (T) this;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public long takePower(long power, boolean simulated) {
		return this.extractEnergy(null, (int) Math.min(power, Integer.MAX_VALUE), simulated);
	}

	@Override
	public boolean canOutputTo(EnumFacing dir) {
		return true;
	}

}