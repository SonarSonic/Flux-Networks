package sonar.flux.connection;

import java.util.ArrayList;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.network.sync.ISyncableListener;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.sync.SyncableList;
import sonar.core.utils.CustomColour;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.FluxPlayer;
import sonar.flux.api.FluxPlayersList;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.INetworkStatistics;
import sonar.flux.api.PlayerAccess;
import sonar.flux.network.NetworkStatistics;

public abstract class FluxNetworkCommon implements IFluxCommon, ISyncableListener {

	public SyncTagType.STRING cachedOwnerName = new SyncTagType.STRING(0), networkName = new SyncTagType.STRING(1);
	public SyncTagType.INT networkID = new SyncTagType.INT(2);
	public SyncEnum<AccessType> accessType = new SyncEnum<AccessType>(AccessType.values(), 3).setDefault(AccessType.PRIVATE);
	public SyncTagType.LONG maxStored = new SyncTagType.LONG(4);
	public SyncTagType.LONG energyStored = new SyncTagType.LONG(5);
	public SyncUUID ownerUUID = new SyncUUID(6);
	public SyncNBTAbstract<CustomColour> colour = new SyncNBTAbstract(CustomColour.class, 7);
	public NetworkStatistics networkStats = new NetworkStatistics();
	public ArrayList<ClientFlux> fluxConnections = Lists.newArrayList();
	public FluxPlayersList players = new FluxPlayersList();
	public SyncableList parts = new SyncableList(this);
	{
		parts.addParts(cachedOwnerName, networkName, networkID, accessType, maxStored, energyStored, ownerUUID, colour, networkStats, players);

		colour.setObject(new CustomColour(41, 94, 138));

	}

	public FluxNetworkCommon() {
	}

	public FluxNetworkCommon(int ID, UUID owner, String name, CustomColour networkColour, AccessType type) {
		ownerUUID.setObject(owner);
		networkID.setObject(ID);
		GameProfile profile = SonarHelper.getProfileByUUID(owner);
		cachedOwnerName.setObject(profile != null ? profile.getName() : "");
		networkName.setObject(name);
		colour.setObject(networkColour);
		accessType.setObject(type);
		players.add(new FluxPlayer(owner, PlayerAccess.OWNER));
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
		return colour.getObject();
	}

	@Override
	public String getNetworkName() {
		return networkName.getObject();
	}

	@Override
	public String getCachedPlayerName() {
		if (cachedOwnerName.getObject() == null || cachedOwnerName.getObject().isEmpty()) {
			GameProfile profile = SonarHelper.getProfileByUUID(ownerUUID.getUUID());
			cachedOwnerName.setObject(profile != null ? profile.getName() : "");
		}
		return cachedOwnerName.getObject();
	}

	@Override
	public UUID getOwnerUUID() {
		return ownerUUID.getUUID();
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
	public void setClientConnections(ArrayList<ClientFlux> flux) {
		this.fluxConnections = (ArrayList<ClientFlux>) flux.clone();
	}

	@Override
	public ArrayList<ClientFlux> getClientFluxConnection() {
		return fluxConnections;
	}

	@Override
	public boolean isFakeNetwork() {
		return false;
	}

	public FluxPlayersList getPlayers() {
		return players;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, parts);
		players.readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.writeSyncParts(nbt, type, parts, false);
		players.writeData(nbt, type);
		return nbt;
	}
}
