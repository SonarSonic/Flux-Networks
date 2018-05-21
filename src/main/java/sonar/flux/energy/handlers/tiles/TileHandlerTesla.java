package sonar.flux.energy.handlers.tiles;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.flux.api.energy.ITileEnergyHandler;
import sonar.flux.api.energy.TileEnergyHandler;

import javax.annotation.Nonnull;

@TileEnergyHandler(modid = "tesla", priority = 2)
public class TileHandlerTesla implements ITileEnergyHandler {

	@Override
	public EnergyType getEnergyType() {
		return EnergyType.TESLA;
	}

	@Override
	public boolean canRenderConnection(@Nonnull TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir) || tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir);
	}

	@Override
	public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir);
	}

	@Override
	public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
		return tile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir);
	}

	@Override
	public long addEnergy(long add, TileEntity tile, EnumFacing dir, ActionType actionType) {
		ITeslaConsumer consumer = tile.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, dir);
		return consumer.givePower(add, actionType.shouldSimulate());
	}

	@Override
	public long removeEnergy(long remove, TileEntity tile, EnumFacing dir, ActionType actionType) {
		ITeslaProducer consumer = tile.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, dir);
		return consumer.takePower(remove, actionType.shouldSimulate());
	}

}
