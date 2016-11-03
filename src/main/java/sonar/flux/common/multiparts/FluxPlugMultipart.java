package sonar.flux.common.multiparts;
/*
import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.SonarLoader;
import sonar.core.utils.IGuiTile;
import sonar.flux.FluxNetworks;
import sonar.flux.client.GuiFluxPlug;
import sonar.flux.common.ContainerFlux;

@Optional.InterfaceList({ 
	@Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla")  })
public class FluxPlugMultipart extends FluxConnectionMultipart implements IGuiTile, IEnergyReceiver, ITeslaConsumer {

	public FluxPlugMultipart() {}
	
	public FluxPlugMultipart(EntityPlayer player) {
		super(player, ConnectionType.PLUG);
		customName.setDefault("Flux Plug");
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerFlux(player, this, false);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiFluxPlug(player, this);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (maxReceive == 0) {
			return 0;
		}
		return (int) (this.getNetwork().receiveEnergy(Math.min(maxReceive, getTransferLimit()), simulate ? ActionType.SIMULATE : ActionType.PERFORM));
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
				//in this situation this is just easier.
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
	public ItemStack getItemStack() {
		return new ItemStack(FluxNetworks.fluxPlug);
	}

}
*/
