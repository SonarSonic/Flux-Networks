package sonar.flux.common.tileentity;

import cofh.redstoneflux.api.IEnergyProvider;
import mekanism.api.energy.IStrictEnergyOutputter;
import net.darkhax.tesla.api.ITeslaProducer;
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
import sonar.flux.api.tiles.IFluxPoint;
import sonar.flux.client.GuiFlux;
import sonar.flux.common.containers.ContainerFlux;

@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla"),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyOutputter", modid = "mekanism"),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux")
})
public class TileEntityPoint extends TileEntityFlux implements IGuiTile, IEnergyProvider, ITeslaProducer, IStrictEnergyOutputter, IEnergyStorage, IFluxPoint {

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
		return new GuiFlux((Container) getGuiContainer(player), this, player);
	}

    public int energyExtract(EnumFacing from, int maxExtract, boolean simulate) {
		if (maxExtract == 0) {
			return 0;
		}
        int extracted = (int) this.getNetwork().extractEnergy(Math.min(maxExtract, getValidTransfer(maxExtract, from)), simulate ? ActionType.SIMULATE : ActionType.PERFORM);
		if (!simulate && !disableLimit.getObject()) {
            this.onEnergyRemoved(from, extracted);
		}
		return extracted;
        //return 0;
    }

    @Override
    @Optional.Method(modid = "redstoneflux")
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return energyExtract(from, maxExtract, simulate);
	}

	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return true;
		}
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_PRODUCER)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY == capability) {
			return (T) this;
		}
		if (SonarLoader.teslaLoaded) {
			if (capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
				return (T) this;
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public long takePower(long power, boolean simulated) {
        return energyExtract(null, (int) Math.min(power, Integer.MAX_VALUE), simulated);
	}

	@Override
    public boolean canOutputEnergy(EnumFacing dir) {
		return true;
	}

    @Override
    public double pullEnergy(EnumFacing side, double amount, boolean simulate) {
        return energyExtract(side, (int) Math.min(amount, Integer.MAX_VALUE), simulate);
    }

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
        return energyExtract(null, Math.min(maxExtract, Integer.MAX_VALUE), simulate);
	}

	@Override
	public int getEnergyStored() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxEnergyStored() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
}