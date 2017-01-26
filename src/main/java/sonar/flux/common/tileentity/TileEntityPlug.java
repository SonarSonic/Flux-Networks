package sonar.flux.common.tileentity;

import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.SonarLoader;
import sonar.core.utils.IGuiTile;
import sonar.flux.client.GuiFlux;
import sonar.flux.common.ContainerFlux;

@Optional.InterfaceList({ @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla") })
public class TileEntityPlug extends TileEntityFlux implements IGuiTile, IEnergyReceiver, ITeslaConsumer {

	public TileEntityPlug() {
		super(ConnectionType.PLUG);
		customName.setDefault("Flux Plug");
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerFlux(player, this, false);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiFlux((Container) getGuiContainer(player), this, player);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (maxReceive == 0) {
			return 0;
		}
		int received = (int) (this.getNetwork().receiveEnergy(Math.min(maxReceive, getCurrentTransferLimit()), simulate ? ActionType.SIMULATE : ActionType.PERFORM));
		if (!simulate && !disableLimit.getObject()) {
			this.onEnergyAdded(received);
		}
		return received;
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
				// in this situation this is just easier.
				return (T) this;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public long givePower(long power, boolean simulated) {
		return this.receiveEnergy(null, (int) Math.min(power, Integer.MAX_VALUE), simulated);
	}
}