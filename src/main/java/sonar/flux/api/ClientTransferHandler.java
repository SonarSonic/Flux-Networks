package sonar.flux.api;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientTransferHandler implements INBTSyncable, ITransferHandler {

	public IFlux flux; // CLIENTFLUX or normal flux
	public List<IFluxTransfer> transfers = new ArrayList<>();
	public long removed;
	public long added;
	public long buffer;

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
		removed = nbt.getLong("r");
		added = nbt.getLong("a");
		buffer = nbt.getLong("buf");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagList list = new NBTTagList();
		transfers.forEach(t -> list.appendTag(t.writeData(new NBTTagCompound(), type)));
		nbt.setTag("transfers", list);
		nbt.setLong("r", removed);
		nbt.setLong("a", added);
		nbt.setLong("buf", buffer);
		return nbt;
	}

	public static ClientTransferHandler getInstanceFromHandler(IFlux flux, ITransferHandler handler) {
		ClientTransferHandler clienthandler = new ClientTransferHandler(flux);
		if (flux.isChunkLoaded()) {
			clienthandler.added = handler.getAdded();
			clienthandler.removed = handler.getRemoved();
			clienthandler.buffer = handler.getBuffer();
			handler.getTransfers().stream().filter(Objects::nonNull).forEach(t -> clienthandler.transfers.add(ClientTransfer.getInstanceFromHandler(clienthandler, t)));
		}
		return clienthandler;
	}

	@Override
	public void onStartServerTick() {}

	@Override
	public void onEndWorldTick() {}

	@Override
	public long getBuffer() {
		return buffer;
	}

	@Override
	public long getAdded() {
		return added;
	}

	@Override
	public long getRemoved() {
		return removed;
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
