package fluxnetworks.api.tileentity;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.ITransferHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.UUID;

/**
 *extended by IFluxPoint and IFluxPlug
 */
public interface IFluxConnector {

    enum ConnectionType {
        POINT,
        PLUG,
        STORAGE,
        CONTROLLER;

        ConnectionType() {

        }

        public boolean canAddEnergy() {
            return this == PLUG;
        }

        public boolean canRemoveEnergy() {
            return this == POINT;
        }

        public boolean isController() {
            return this == CONTROLLER;
        }
    }

    int getNetworkID();

    int getPriority();

    IFluxNetwork getNetwork();

    UUID getConnectionOwner();

    ConnectionType getConnectionType();

    long getCurrentLimit();

    boolean isActive();

    boolean isChunkLoaded();

    void connect(IFluxNetwork network);

    void disconnect(IFluxNetwork network);

    ITransferHandler getTransferHandler();

    World getDimension();

    ItemStack getDisplayStack();
}
