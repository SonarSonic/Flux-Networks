package sonar.flux.connection;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.utils.CustomColour;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.INetworkStatistics;
import sonar.flux.network.NetworkStatistics;

public abstract class FluxNetworkCommon implements IFluxCommon {

	public NetworkStatistics networkStats = new NetworkStatistics();
	public SyncTagType.STRING ownerName = new SyncTagType.STRING(0), networkName = new SyncTagType.STRING(1);
	public SyncTagType.INT networkID = new SyncTagType.INT(2);
	public SyncEnum<AccessType> accessType = new SyncEnum<AccessType>(AccessType.values(), 3).setDefault(AccessType.PRIVATE);
	public SyncTagType.LONG maxStored = new SyncTagType.LONG(4);
	public SyncTagType.LONG energyStored = new SyncTagType.LONG(5);

	//public SyncTagType.INT colour = new SyncTagType.INT(4);
	public CustomColour colour = new CustomColour(0, 0, 0);
	public final ArrayList<ISyncPart> parts = new ArrayList();
	public ArrayList<ClientFlux> fluxConnections = new ArrayList();

	public FluxNetworkCommon(NBTTagCompound tag) {
		parts.addAll(Arrays.asList(ownerName, networkName, networkID, accessType, maxStored, energyStored));
		readData(tag, SyncType.SAVE);
	}

	public FluxNetworkCommon(int ID, String owner, String name, CustomColour colour, AccessType type) {
		parts.addAll(Arrays.asList(ownerName, networkName, networkID, accessType, maxStored, energyStored));
		networkID.setObject(ID);
		ownerName.setObject(owner);
		networkName.setObject(name);
		this.colour = colour;
		accessType.setObject(type);
	}

	@Override
	public AccessType getAccessType() {
		return accessType.getObject();
	}

	@Override
	public int getNetworkID() {
		return networkID.getObject();
	}

	@Override
	public CustomColour getNetworkColour() {
		return colour;
	}

	@Override
	public String getNetworkName() {
		return networkName.getObject();
	}

	@Override
	public String getOwnerName() {
		return ownerName.getObject();
	}

	@Override
	public INetworkStatistics getStatistics() {
		return networkStats;
	}
	
	@Override
	public long getEnergyAvailable() {
		return energyStored.getObject();
	}

	@Override
	public long getMaxEnergyStored() {
		return maxStored.getObject();
	}
	
	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, parts);
		if (nbt.hasKey("stats"))
			networkStats.readData(nbt.getCompoundTag("stats"), SyncType.SAVE);
		colour.readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.writeSyncParts(nbt, type, parts, false);
		NBTTagCompound statsTag = new NBTTagCompound();
		networkStats.writeData(statsTag, SyncType.SAVE);
		if (!statsTag.hasNoTags())
			nbt.setTag("stats", statsTag);
		colour.writeData(nbt, type);
		return nbt;
	}

	@Override
	public void setClientConnections(ArrayList<ClientFlux> flux) {
		this.fluxConnections=(ArrayList<ClientFlux>) flux.clone();
	}

	@Override
	public ArrayList<ClientFlux> getClientFluxConnection() {
		return fluxConnections;
	}

	@Override
	public boolean isFakeNetwork() {
		return false;
	}

}
