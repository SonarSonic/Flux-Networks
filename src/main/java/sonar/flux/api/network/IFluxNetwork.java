package sonar.flux.api.network;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.api.AdditionType;
import sonar.flux.api.RemovalType;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxListenable;

/**
 * all server stored Networks will implement this
 */
public interface IFluxNetwork extends IFluxCommon {

    /**
     * called every tick like TileEntities, you shouldn't be calling this method
     */
    void onStartServerTick();
    
    void onEndServerTick();

    /**
     * returns true if a {@link IFluxController} has been connected
     */
    boolean hasController();

    /**
     * obtains the IFluxController currently connected, can be null
     */
    IFluxController getController();

    /**
     * sets the custom name of this network
     */
    void setNetworkName(String name);

    /**
     * sets the access setting of this network
     */
    void setAccessType(AccessType type);

    /**
     * sets the colour of this network
     */
    void setCustomColour(CustomColour colour);
    
    void setDisableConversion(boolean disable);
    
    void setDefaultEnergyType(EnergyType type);

    void markTypeDirty(FluxCache... caches);

    void setHasConnections(boolean bool);

    <T extends IFluxListenable> List<T> getConnections(FluxCache<T> type);

    void markDirty();

    /**
     * checks if the given player as an access type already as
     */
    PlayerAccess getPlayerAccess(EntityPlayer player);

    /**
     * removes access to the network from the given player, points/plugs associated with them and on the network will then be blocked
     */
    void removePlayerAccess(UUID playerUUID, PlayerAccess access);

    /**
     * adds access to the network for a given player, the owner is added as default
     */
    void addPlayerAccess(UUID playerUUID, PlayerAccess access);

    /**
     * used for pushing energy into the network it returns the amount received
     * @param energyType TODO
     */
    long addPhantomEnergyToNetwork(long maxReceive, EnergyType energyType, ActionType type);

    /**
     * used for pulling energy from the network it returns the amount extracted
     * @param energyType TODO
     */
    long removePhantomEnergyFromNetwork(long maxExtract, EnergyType energyType, ActionType type);

    /**
     * adds a Flux Connection to the network, this could be a PLUG, POINT or STORAGE
     * @param type TODO
     */
    void addConnection(IFluxListenable flux, AdditionType type);

    /**
     * removes a Flux Connection from the network
     * @param type TODO
     */
    void removeConnection(IFluxListenable flux, RemovalType type);

    void buildFluxConnections();
    
    void addFluxListener(IFluxListenable listener);
    
    void removeFluxListener(IFluxListenable listener);

    List<IFluxListenable> getFluxListeners();
    
    IFluxNetwork updateNetworkFrom(IFluxNetwork network);

    void onRemoved();
    
    boolean canConvert(EnergyType from, EnergyType to);
    
    boolean canTransfer(EnergyType type);
}