package sonar.flux.common.tileentity.energy;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.integration.EUHelper;
import sonar.flux.api.energy.ITransferHandler;
@Optional.InterfaceList({ 
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "IC2"), 
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2"),
	@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2") })

public abstract class TileFluxIC2 extends TileFluxForgeEnergy implements IEnergyTile, IEnergySink, IEnergySource {

	public TileFluxIC2(ConnectionType type) {
		super(type);
	}

	///// * IC2 *//////
	boolean IC2Connected = false;
	
	@Override
	@Optional.Method(modid = "IC2")
	public void onLoad() {
		super.onLoad();
		if (!this.getWorld().isRemote && !IC2Connected) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			IC2Connected = true;
		}
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void invalidate() {
		super.invalidate();
		onChunkUnload();
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void onChunkUnload() {
		super.onChunkUnload();
		if (!this.getWorld().isRemote && IC2Connected) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			IC2Connected = false;
		}
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double getDemandedEnergy() {
		return addPhantomEnergyToNetwork(null, (long) EUHelper.getVoltage(getSinkTier()), EnergyType.EU, ActionType.SIMULATE);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public int getSinkTier() {
		return 4;
	}

	@Override
	@Optional.Method(modid = "IC2")
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return getConnectionType().canAddPhantomPower();
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		return addPhantomEnergyToNetwork(null, (long) amount, EnergyType.EU, ActionType.PERFORM);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
		return getConnectionType().canRemovePhantomPower();
	}

	@Override
	@Optional.Method(modid = "IC2")
	public double getOfferedEnergy() {
		return removePhantomEnergyFromNetwork(null, (long) EUHelper.getVoltage(getSinkTier()), EnergyType.EU, ActionType.SIMULATE);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public void drawEnergy(double amount) {
		removePhantomEnergyFromNetwork(null, (long) amount, EnergyType.EU, ActionType.PERFORM);
	}

	@Override
	@Optional.Method(modid = "IC2")
	public int getSourceTier() {
		return 4;
	}

}
