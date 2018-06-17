package sonar.flux.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.SonarHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.client.gui.tabs.GuiTabFluxConnectorIndex;
import sonar.flux.common.tileentity.energy.TileFluxTesla;
import sonar.flux.connection.transfer.handlers.ConnectionTransferHandler;

import javax.annotation.Nonnull;
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
		boolean sendRenderUpdate = false;
		for (EnumFacing face : faces) {
			BlockPos adj = pos.offset(face);
			TileEntity tile = world.getTileEntity(adj);
			boolean original = connections.getObjects().get(face.getIndex());
			boolean current = FluxNetworks.TRANSFER_HANDLER.canRenderConnection(tile, face.getOpposite());
			if(original != current){
				connections.getObjects().set(face.getIndex(), current);
				sendRenderUpdate = true;
			}
		}
		if(sendRenderUpdate) {
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
