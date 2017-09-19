package sonar.flux.api.tiles;

import java.util.UUID;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.flux.api.network.IFluxNetwork;

/**
 * extended by IFluxPoint & IFluxPlug you must use them if you wish to send and receive energy from the network
 */
public interface IFlux {

    enum ConnectionType {
        POINT, PLUG, STORAGE, CONTROLLER;

        public boolean canSend() {
            return this == PLUG || this == STORAGE;
        }

        public boolean canReceive() {
            return this == POINT || this == STORAGE || this == CONTROLLER;
        }
    }

    int getNetworkID();

    /**
     * the network this Flux connection is a part of
     */
    IFluxNetwork getNetwork();

    UUID getConnectionOwner();

    /**
     * the dimension in which this Flux Connection is located
     */
    World getDimension();

    /**
     * the location of the Flux Connection
     */
    BlockCoords getCoords();

    /**
     * the type of Flux Connection
     */
    ConnectionType getConnectionType();

    /**
     * the maximum RF/t this Flux connection can receive
     */
    long getTransferLimit();

    /**
     * the current RF/t this Flux connection can receive
     */
    long getCurrentTransferLimit();

    long getCurrentTransfer(EnumFacing face);

    long getValidTransfer(long valid, EnumFacing face);

    void onEnergyRemoved(EnumFacing face, long remove);

    void onEnergyAdded(EnumFacing face, long added);

    void setMaxSend(long send);

    void setMaxReceive(long receive);

    /**
     * the higher the priority the sooner the Flux connection will receive power
     */
    int getCurrentPriority();

    /**
     * the custom name is assigned by the user, this allows easier identification of various Flux connections.
     */
    String getCustomName();

    TileEntity[] cachedTiles();

    boolean canTransfer();

    void updateNeighbours(boolean full);

    void connect(IFluxNetwork network);

    void disconnect(IFluxNetwork network);
}
