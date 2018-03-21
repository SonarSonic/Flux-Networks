package sonar.flux.api;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.connection.EmptyFluxNetwork;

public class ClientFlux implements IFlux, INBTSyncable {

	public BlockCoords coords;
	public ConnectionType connection_type;
	public boolean isChunkLoaded = true;
	public int priority;
	public long limit;
	public String customName;
	public ClientTransferHandler handler;

	public ClientFlux(IFlux flux) {
		this.coords = flux.getCoords();
		this.connection_type = flux.getConnectionType();
		this.priority = flux.getCurrentPriority();
		this.limit = flux.getTransferLimit();
		this.customName = flux.getCustomName();
		this.handler = ClientTransferHandler.getInstanceFromHandler(flux, flux.getTransferHandler());
	}

	public ClientFlux(BlockCoords coords, ConnectionType type, int priority, long limit, String customName, ClientTransferHandler handler) {
		this.coords = coords;
		this.connection_type = type;
		this.priority = priority;
		this.limit = limit;
		this.customName = customName;
		this.handler = handler;
	}

	public ClientFlux(NBTTagCompound tag) {
		readData(tag, SyncType.SAVE);
	}
	
	public void setChunkLoaded(boolean isChunkLoaded){
		this.isChunkLoaded = isChunkLoaded;		
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		coords = BlockCoords.readFromNBT(nbt);
		connection_type = ConnectionType.values()[nbt.getInteger("type")];
		priority = nbt.getInteger("priority");
		limit = nbt.getLong("limit");
		customName = nbt.getString("name");
		handler = new ClientTransferHandler(this);
		handler.readData(nbt.getCompoundTag("handler"), type);
		isChunkLoaded = nbt.getBoolean("isChunkLoaded");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		BlockCoords.writeToNBT(nbt, coords);
		nbt.setInteger("type", connection_type.ordinal());
		nbt.setInteger("priority", priority);
		nbt.setLong("limit", limit);
		nbt.setString("name", customName);
		nbt.setTag("handler", handler.writeData(new NBTTagCompound(), type));
		nbt.setBoolean("isChunkLoaded", isChunkLoaded);
		return nbt;
	}

	public void addToGuiList(List list, boolean origin, boolean transfers) {
		if (origin)
			list.add(this);
		if (transfers && isChunkLoaded())
			handler.getTransfers().forEach(t -> list.add(t));
	}
	
	public int getDimensionID(){
		return coords.getDimension();
	}

	@Override
	public World getDimension() {
		return coords.getWorld();
	}

	@Override
	public BlockCoords getCoords() {
		return coords;
	}

	@Override
	public IFluxNetwork getNetwork() {
		return EmptyFluxNetwork.INSTANCE;
	}

	@Override
	public ConnectionType getConnectionType() {
		return connection_type;
	}

	@Override
	public long getTransferLimit() {
		return limit;
	}

	@Override
	public int getCurrentPriority() {
		return priority;
	}

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public UUID getConnectionOwner() {
		return null;
	}

	@Override
	public int getNetworkID() {
		return -1;
	}

	@Override
	public void connect(IFluxNetwork network) {}

	@Override
	public void disconnect(IFluxNetwork network) {}

	@Override
	public void setMaxSend(long send) {}

	@Override
	public void setMaxReceive(long receive) {}

	@Override
	public PlayerAccess canAccess(EntityPlayer player) {
		return null;
	}

	@Override
	public ITransferHandler getTransferHandler() {
		return handler;
	}

	@Override
	public boolean isChunkLoaded() {
		return isChunkLoaded;
	}
}
