package sonar.flux.connection;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.*;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.FluxPlayersList;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.connection.transfer.stats.NetworkStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class FluxNetworkCommon implements IFluxCommon, ISyncableListener {

	public SyncTagType.STRING cachedOwnerName = new SyncTagType.STRING(0), networkName = new SyncTagType.STRING(1);
	public SyncTagType.INT networkID = new SyncTagType.INT(2);
    public SyncEnum<AccessType> accessType = new SyncEnum<>(AccessType.values(), 3).setDefault(AccessType.PRIVATE);
	//public SyncTagType.LONG maxStored = new SyncTagType.LONG(4);
	//public SyncTagType.LONG energyStored = new SyncTagType.LONG(5);
	private SyncUUID ownerUUID = new SyncUUID(6);
	public SyncNBTAbstract<CustomColour> colour = new SyncNBTAbstract(CustomColour.class, 7);
	public SyncTagType.BOOLEAN disableConversion = new SyncTagType.BOOLEAN(8);
	public SyncEnergyType defaultEnergyType = new SyncEnergyType(8);
	public NetworkStatistics networkStats = new NetworkStatistics(this);
    public List<ClientFlux> fluxConnections = new ArrayList<>();
	public FluxPlayersList players = new FluxPlayersList();
	public SyncableList parts = new SyncableList(this);

	{
		parts.addParts(cachedOwnerName, networkName, networkID, accessType, ownerUUID, colour, disableConversion, defaultEnergyType, networkStats, players);
		colour.setObject(new CustomColour(41, 94, 138));
	}

	public FluxNetworkCommon() {
	}

	public FluxNetworkCommon(int ID, UUID playerUUID, String playerName, String name, CustomColour networkColour, AccessType type, boolean disableConvert, EnergyType defaultEnergy) {
		ownerUUID.setObject(playerUUID);
		networkID.setObject(ID);
		cachedOwnerName.setObject(playerName);
		networkName.setObject(name);
		colour.setObject(networkColour);
		accessType.setObject(type);
		disableConversion.setObject(disableConvert);
		defaultEnergyType.setEnergyType(defaultEnergy);
	}

	@Override
	public boolean isOwner(EntityPlayer player){
		UUID onlineID = FluxPlayer.getOnlineUUID(player);
		if(onlineID.equals(ownerUUID.getUUID())){
			return true;
		}
		UUID offlineID = FluxPlayer.getOfflineUUID(player);
		if(offlineID.equals(getOwnerUUID())){
			return true;
		}
		return false;
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
		return cachedOwnerName.getObject();
	}

	@Override
	public UUID getOwnerUUID() {
		return ownerUUID.getUUID();
	}

	@Override
	public boolean disabledConversion() {
		return this.disableConversion.getObject();
	}

	@Override
	public EnergyType getDefaultEnergyType() {
		return this.defaultEnergyType.getEnergyType();
	}
	
	@Override
	public NetworkStatistics getStatistics() {
		return networkStats;
	}

	@Override
	public long getEnergyAvailable() {
		return this.getStatistics().network_energy;
	}

	@Override
	public long getMaxEnergyStored() {
		return this.getStatistics().network_energy_capacity;
	}

	@Override
	public void setClientConnections(List<ClientFlux> flux) {
		this.fluxConnections = Lists.newArrayList(flux);
	}

	@Override
	public List<ClientFlux> getClientFluxConnection() {
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
