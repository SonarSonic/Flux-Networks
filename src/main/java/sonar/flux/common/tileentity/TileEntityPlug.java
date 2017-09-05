package sonar.flux.common.tileentity;

import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.SonarLoader;
import sonar.core.utils.IGuiTile;
import sonar.flux.api.tiles.IFluxPlug;
import sonar.flux.client.GuiFlux;
import sonar.flux.common.ContainerFlux;

@Optional.InterfaceList({ @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla") })
public class TileEntityPlug extends TileEntityFlux implements IGuiTile, IEnergyReceiver, ITeslaConsumer, IEnergyStorage, IFluxPlug {

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
		/*
		if (maxReceive == 0) {
			return 0;
		}
		int received = (int) (getNetwork().receiveEnergy(Math.min(maxReceive, getValidTransfer(maxReceive, from)), simulate ? ActionType.SIMULATE : ActionType.PERFORM));
		if (!simulate){// && !disableLimit.getObject()) {
			this.onEnergyAdded(from, received);
		}
		
		return received;
		*/
		return 0;
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return true;
		}
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_CONSUMER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return (T) this;
		}
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

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return this.receiveEnergy(null, (int) Math.min(maxReceive, Integer.MAX_VALUE), simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
}