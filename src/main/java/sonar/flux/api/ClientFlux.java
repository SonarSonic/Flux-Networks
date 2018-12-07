package sonar.flux.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.energy.internal.ITransferHandler;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.api.tiles.IFlux;
import sonar.flux.connection.FluxNetworkInvalid;

import java.util.List;
import java.util.UUID;

public class ClientFlux implements IFlux, INBTSyncable {

	public BlockCoords coords;
	public ConnectionType connection_type;
	public boolean isChunkLoaded = true;
	public int network_id;
	public int priority;
	public int folder_id;
	public long limit;
	public String customName;
	public ItemStack stack;
	public boolean disableLimit;
	public EnumActivationType activation_type;
	public EnumPriorityType priority_type;

	public ClientTransferHandler handler;

	public ClientFlux(IFlux flux) {
		this.coords = flux.getCoords();
		this.connection_type = flux.getConnectionType();
		this.network_id = flux.getNetworkID();
		this.priority = flux.getCurrentPriority();
		this.folder_id = flux.getFolderID();
		this.limit = flux.getTransferLimit();
		this.customName = flux.getCustomName();
		this.handler = ClientTransferHandler.getInstanceFromHandler(flux, flux.getTransferHandler());
		this.stack = flux.getDisplayStack();
		this.disableLimit = flux.getDisableLimit();
		this.activation_type = flux.getActivationType();
		this.priority_type = flux.getPriorityType();
	}

	public ClientFlux(BlockCoords coords, ConnectionType type, int network_id, int priority, int folder_id, long limit, String customName, boolean disableLimit, EnumActivationType activation_type, EnumPriorityType priority_type, ClientTransferHandler handler, ItemStack stack) {
		this.coords = coords;
		this.connection_type = type;
		this.network_id = network_id;
		this.priority = priority;
		this.folder_id = folder_id;
		this.limit = limit;
		this.customName = customName;
		this.handler = handler;
		this.stack = stack;
		this.disableLimit = disableLimit;
		this.activation_type = activation_type;
		this.priority_type = priority_type;
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
		network_id = nbt.getInteger("n_id");
		priority = nbt.getInteger("priority");
		folder_id = nbt.getInteger("folder_id");
		limit = nbt.getLong("limit");
		customName = nbt.getString("name");
		handler = new ClientTransferHandler(this);
		handler.readData(nbt.getCompoundTag("handler"), type);
		isChunkLoaded = nbt.getBoolean("isChunkLoaded");
		disableLimit = nbt.getBoolean("dLimit");
		activation_type = EnumActivationType.values()[nbt.getInteger("a_type")];
		priority_type = EnumPriorityType.values()[nbt.getInteger("p_type")];

		stack = new ItemStack(nbt);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		BlockCoords.writeToNBT(nbt, coords);
		nbt.setInteger("type", connection_type.ordinal());
		nbt.setInteger("n_id", network_id);
		nbt.setInteger("priority", priority);
		nbt.setInteger("folder_id", folder_id);
		nbt.setLong("limit", limit);
		nbt.setString("name", customName);
		nbt.setTag("handler", handler.writeData(new NBTTagCompound(), type));
		nbt.setBoolean("isChunkLoaded", isChunkLoaded);
		nbt.setBoolean("dLimit", disableLimit);
		nbt.setInteger("a_type", activation_type.ordinal());
		nbt.setInteger("p_type", priority_type.ordinal());

		stack.writeToNBT(nbt);
		return nbt;
	}

	public void addToGuiList(List list, boolean origin, boolean transfers) {
		if (origin)
			list.add(this);
		if (transfers && isChunkLoaded())
			list.addAll(handler.getTransfers());
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
		return FluxNetworkInvalid.INVALID;
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
	public long getCurrentLimit() {
		return limit;//if we are mirroring the server this should be disableLimit ? Long.MAX_VALUE : limit;
	}

	@Override
	public boolean getDisableLimit() {
		return disableLimit;
	}

	@Override
	public EnumActivationType getActivationType() {
		return activation_type;
	}

	@Override
	public EnumPriorityType getPriorityType() {
		return priority_type;
	}

	@Override
	public int getCurrentPriority() {
		return priority;
	}

	@Override
	public int getFolderID() {
		return folder_id;
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
		return network_id;
	}

	@Override
	public void connect(IFluxNetwork network) {}

	@Override
	public void disconnect(IFluxNetwork network) {}

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

	@Override
	public boolean isActive() {
		return false; //TODO ?
	}

	@Override
	public ItemStack getDisplayStack() {
		return stack;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof ClientFlux){
			return ((ClientFlux)obj).getCoords().equals(getCoords());
		}
		return false;
	}
}
