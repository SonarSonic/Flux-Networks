package sonar.flux.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants.NBT;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.energy.internal.IFluxTransfer;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.connection.transfer.handlers.BaseTransferHandler;

public class ClientTransferHandler implements INBTSyncable, ITransferHandler {

	public IFlux flux; // CLIENTFLUX or normal flux
	public List<IFluxTransfer> transfers = new ArrayList<>();
	public long max_remove;
	public long max_add;
	public long add_limit;
	public long remove_limit;

	public ClientTransferHandler(IFlux flux) {
		this.flux = flux;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTTagList list = nbt.getTagList("transfers", NBT.TAG_COMPOUND);
		transfers.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			ClientTransfer trans = new ClientTransfer(this);
			trans.readData(tag, type);
			transfers.add(trans);
		}
		max_remove = nbt.getLong("r");
		max_add = nbt.getLong("a");
		add_limit = nbt.getLong("al");
		remove_limit = nbt.getLong("rl");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagList list = new NBTTagList();
		transfers.forEach(t -> list.appendTag(t.writeData(new NBTTagCompound(), type)));
		nbt.setTag("transfers", list);
		nbt.setLong("r", max_remove);
		nbt.setLong("a", max_add);
		nbt.setLong("al", add_limit);
		nbt.setLong("rl", remove_limit);
		return nbt;
	}

	public static ClientTransferHandler getInstanceFromHandler(IFlux flux, ITransferHandler handler) {
		ClientTransferHandler clienthandler = new ClientTransferHandler(flux);
		if (flux.isChunkLoaded()) {
			if (handler instanceof BaseTransferHandler) {
				BaseTransferHandler base_handler = (BaseTransferHandler) handler;
				clienthandler.max_add = base_handler.max_add;
				clienthandler.max_remove = base_handler.max_remove;
				clienthandler.add_limit = base_handler.add_limit;
				clienthandler.remove_limit = base_handler.remove_limit;
			}
			handler.getTransfers().stream().filter(h -> h != null).forEach(t -> clienthandler.transfers.add(ClientTransfer.getInstanceFromHandler(clienthandler, t)));
		}
		return clienthandler;
	}

	@Override
	public void onStartServerTick() {}

	@Override
	public void onEndWorldTick() {}

	@Override
	public long getAdded() {
		return add_limit - max_add;
	}

	@Override
	public long getRemoved() {
		return remove_limit - max_remove;
	}

	@Override
	public boolean hasTransfers() {
		return !transfers.isEmpty();
	}

	@Override
	public void updateTransfers(EnumFacing... face) {}

	@Override
	public List<IFluxTransfer> getTransfers() {
		return transfers;
	}

	@Override
	public long addToNetwork(long maxTransferRF, EnergyType type, ActionType actionType) {
		return 0;
	}

	@Override
	public long removeFromNetwork(long maxTransferRF, EnergyType type, ActionType actionType) {
		return 0;
	}

}
