package sonar.flux.connection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.NBTHelper;
import sonar.core.sync.ISonarValue;
import sonar.core.sync.ISyncValue;
import sonar.core.sync.SyncRegistry;
import sonar.core.sync.ValueWatcher;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.NetworkFluxFolder;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.connection.transfer.stats.NetworkStatistics;
import sonar.flux.network.NetworkData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class FluxNetworkBase extends ValueWatcher implements IFluxNetwork {

    //// SYNCED VALUES \\\\
    public ISyncValue<Integer> network_id = SyncRegistry.createValue(Integer.class, this, NetworkData.NETWORK_ID);
    public ISyncValue<String> network_name = SyncRegistry.createValue(String.class, this, NetworkData.NETWORK_NAME);
    public ISyncValue<AccessType> network_access = SyncRegistry.createValue(AccessType.class, this, NetworkData.ACCESS);
    public ISyncValue<CustomColour> network_colour = SyncRegistry.createValue(CustomColour.class, this, NetworkData.COLOUR);
    public ISyncValue<UUID> network_owner = SyncRegistry.createValue(UUID.class, this, NetworkData.OWNER_UUID);
    public ISyncValue<Boolean> network_conversion = SyncRegistry.createValue(Boolean.class, this, NetworkData.CONVERSION);
    public ISyncValue<EnergyType> network_energy_type = SyncRegistry.createValue(EnergyType.class, this, NetworkData.ENERGY_TYPE);
    public ISyncValue<String> cached_player_name = SyncRegistry.createValue(String.class, this, NetworkData.CACHE_PLAYER);

    //// SIMPLE VALUES \\\\
    public ISonarValue<List<FluxPlayer>> network_players = SyncRegistry.createSonarControlledList(FluxPlayer.class, this, new ArrayList<>());
    public ISonarValue<NetworkStatistics> network_stats = SyncRegistry.createSonarValue(NetworkStatistics.class,this, new NetworkStatistics(this));
    public ISonarValue<List<ClientFlux>> client_connections = SyncRegistry.createSonarValueList(ClientFlux.class,this, new ArrayList<>());
    public ISonarValue<List<ClientFlux>> unloaded_connections = SyncRegistry.createSonarValueList(ClientFlux.class, this, new ArrayList<>());
    public ISonarValue<List<NetworkFluxFolder>> network_folders = SyncRegistry.createSonarValueList(NetworkFluxFolder.class, this, new ArrayList<>());

    public FluxNetworkBase() {}

    public FluxNetworkBase(int ID, UUID owner, String cached_player, String name, CustomColour colour, AccessType type, boolean conversion, EnergyType energy_type) {
        network_id.setValueInternal(ID);
        network_name.setValueInternal(name);
        network_access.setValueInternal(type);
        network_colour.setValueInternal(colour);
        network_owner.setValueInternal(owner);
        network_conversion.setValueInternal(conversion);
        network_energy_type.setValueInternal(energy_type);
        cached_player_name.setValueInternal(cached_player);
    }

    //// NETWORK SETTINGS \\\\

    @Override
    public <T> T getSetting(NetworkSettings<T> setting){
        return getSyncSetting(setting).getValue();
    }

    @Override
    public <T> ISonarValue<T> getSyncSetting(NetworkSettings<T> setting){
        return setting.getSyncValue(this);
    }

    @Override
    public PlayerAccess getPlayerAccess(EntityPlayer player){
        if (FluxHelper.isPlayerAdmin(player)) {
            return PlayerAccess.CREATIVE;
        }
        if (isOwner(player)) {
            return PlayerAccess.OWNER;
        }
        if (network_access.getValue() == AccessType.PUBLIC) {
            return PlayerAccess.SHARED_OWNER;
        }
        if (network_access.getValue() == AccessType.RESTRICTED) {
            for (FluxPlayer fluxPlayer : network_players.getValue()) {
                if (fluxPlayer.matches(player)) {
                    return fluxPlayer.getAccess();
                }
            }
        }
        return PlayerAccess.BLOCKED;
    }

    @Override
    public boolean isOwner(EntityPlayer player){
        UUID onlineID = FluxPlayer.getOnlineUUID(player);
        if(onlineID.equals(network_owner.getValue())){
            return true;
        }
        UUID offlineID = FluxPlayer.getOfflineUUID(player);
        if(offlineID.equals(network_owner.getValue())){
            return true;
        }
        return false;
    }

    public void readData(NBTTagCompound nbt, NBTHelper.SyncType type){
        forEachSyncable(sv -> {
            if(sv.canLoadFrom(nbt)) {
                sv.load(nbt);
            }
        });
        if(type.isType(NBTHelper.SyncType.PACKET, NBTHelper.SyncType.SPECIAL)){
            NetworkData.readPlayers(this, nbt);
            NetworkData.readConnections(client_connections, "client_c", this, nbt);
            NetworkData.readFolders(this, nbt);
        }
    }

    public NBTTagCompound writeData(NBTTagCompound nbt, NBTHelper.SyncType type){
        forEachSyncable(sv -> {
            if(type.mustSync() || sv.isDirty()) {
                sv.save(nbt);
            }
        });
        if(type.isType(NBTHelper.SyncType.PACKET, NBTHelper.SyncType.SPECIAL)){
            if(network_players.isDirty() || type.isType(NBTHelper.SyncType.PACKET)) {
                NetworkData.writePlayers(this, nbt);
            }
            if(client_connections.isDirty() || type.isType(NBTHelper.SyncType.PACKET)) {
                NetworkData.writeConnections(client_connections, "client_c", this, nbt);
            }
            if(network_folders.isDirty() || type.isType(NBTHelper.SyncType.PACKET)) {
                NetworkData.writeFolders(this, nbt);
            }
        }

        return nbt;
    }
}