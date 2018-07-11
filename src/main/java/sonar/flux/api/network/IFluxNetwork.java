package sonar.flux.api.network;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.sync.ISonarValue;
import sonar.flux.api.AdditionType;
import sonar.flux.api.RemovalType;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.connection.NetworkSettings;

import java.util.Optional;
import java.util.UUID;

/**
 * all server stored Networks will implement this
 */
public interface IFluxNetwork extends INBTSyncable {

    default int getNetworkID(){
        return getSetting(NetworkSettings.NETWORK_ID);
    }

    <T> T getSetting(NetworkSettings<T> setting);

    <T> ISonarValue<T> getSyncSetting(NetworkSettings<T> setting);

    default <T> void setSetting(NetworkSettings<T> setting, T set){
        getSyncSetting(setting).setValue(set);
    }

    default <T> void setSettingInternal(NetworkSettings<T> setting, T set){
        getSyncSetting(setting).setValueInternal(set);
    }

    /** called every tick like TileEntities, you shouldn't be calling this method */
    default void onStartServerTick(){}

    /** called every tick like TileEntities, you shouldn't be calling this method */
    default void onEndServerTick(){}

    default void onRemoved(){}

    /** adds a Flux Connection to the network, this could be a PLUG, POINT or STORAGE */
    default void queueConnectionAddition(IFluxListenable flux, AdditionType type){}

    /** removes a Flux Connection from the network */
    default void queueConnectionRemoval(IFluxListenable flux, RemovalType type){}

    //<T extends IFluxListenable> List<T> getConnections(FluxCache<T> type);

    boolean isOwner(EntityPlayer player);

    /** checks if the given player as an access type already as */
    PlayerAccess getPlayerAccess(EntityPlayer player);

    /** removes access to the network from the given player,
     * points/plugs associated with them and on the network will then be blocked */
    void removePlayerAccess(UUID uuid, PlayerAccess access);

    /** adds access to the network for a given player, the owner is added as default */
    void addPlayerAccess(String username, PlayerAccess access);

    Optional<FluxPlayer> getValidFluxPlayer(UUID uuid);

    boolean isFakeNetwork();

    default void buildFluxConnections(){}
    
    default boolean canConvert(EnergyType from, EnergyType to){
        return false;
    }
    
    default boolean canTransfer(EnergyType type){
        return false;
    }
    
    default void debugConnectedBlocks(){}
    
    default void debugValidateFluxConnections(){}
}