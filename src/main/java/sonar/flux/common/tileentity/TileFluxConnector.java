package sonar.flux.common.tileentity;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.SonarHelper;
import sonar.flux.api.energy.ITransferHandler;
import sonar.flux.client.GuiTab;
import sonar.flux.common.containers.ContainerFlux;
import sonar.flux.common.tileentity.energy.TileFluxTesla;
import sonar.flux.connection.transfer.handlers.ConnectionTransferHandler;

public abstract class TileFluxConnector extends TileFluxTesla implements IFlexibleGui {

	public final ConnectionTransferHandler handler = new ConnectionTransferHandler(this, this, SonarHelper.convertArray(getValidFaces()));
	
	public TileFluxConnector(ConnectionType type) {
		super(type);
	}
	
	@Override
	public long addPhantomEnergyToNetwork(EnumFacing from, long max_add, EnergyType energy_type, ActionType type) {
		return getConnectionType().canAddPhantomPower() ? handler.addPhantomEnergyToNetwork(from, max_add, energy_type, type) : 0;
    }

	@Override
    public long removePhantomEnergyFromNetwork(EnumFacing from, long max_add, EnergyType energy_type, ActionType type) {
		return getConnectionType().canRemovePhantomPower() ? handler.removePhantomEnergyFromNetwork(from, max_add, energy_type, type) : 0;
    }

	@Override
	public ITransferHandler getTransferHandler() {
		return handler;
	}
}
