package sonar.flux.common.tileentity;

import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.SonarHelper;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.client.GuiTab;
import sonar.flux.client.tabs.GuiTabFluxConnectorIndex;
import sonar.flux.common.tileentity.energy.TileFluxTesla;
import sonar.flux.connection.transfer.handlers.ConnectionTransferHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
		//PHANTOM REMOVAL IS DISCOURAGED
		return 0;//getConnectionType().canRemovePhantomPower() ? handler.removePhantomEnergyFromNetwork(from, max_add, energy_type, type) : 0;
    }

	@Override
	public void updateTransfers(EnumFacing ...faces){
		super.updateTransfers(faces);
		if(handler.wasChanged){
			ArrayList<Boolean> bool = new ArrayList<>();
			for(EnumFacing face : EnumFacing.VALUES){
				bool.add(handler.transfers.get(face)!=null);
			}
			connections.setObjects(bool);
			SonarCore.sendFullSyncAroundWithRenderUpdate(this, 128);
		}
	}

	@Override
	public ITransferHandler getTransferHandler() {
		return handler;
	}

	@Nonnull
	public Object getIndexScreen(List<GuiTab> tabs){
		return new GuiTabFluxConnectorIndex(this, tabs);
	}
}
