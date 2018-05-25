package sonar.flux.api.network;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.api.energy.EnergyType;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.api.AdditionType;
import sonar.flux.api.RemovalType;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxListenable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    void removePlayerAccess(UUID uuid, PlayerAccess access);

    /**
     * adds access to the network for a given player, the owner is added as default
     */
    void addPlayerAccess(String username, PlayerAccess access);

    Optional<FluxPlayer> getValidFluxPlayer(UUID uuid);

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

    void changeConnection(IFluxListenable flux);

    void buildFluxConnections();
    
    void addFluxListener(IFluxListenable listener);
    
    void removeFluxListener(IFluxListenable listener);

    List<IFluxListenable> getFluxListeners();
    
    IFluxNetwork updateNetworkFrom(IFluxNetwork network);

    void onRemoved();
    
    boolean canConvert(EnergyType from, EnergyType to);
    
    boolean canTransfer(EnergyType type);
    
    void debugConnectedBlocks();
    
    void debugValidateFluxConnections();
}