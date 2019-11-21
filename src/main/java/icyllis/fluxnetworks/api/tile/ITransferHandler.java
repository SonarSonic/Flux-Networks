package icyllis.fluxnetworks.api.tile;

import icyllis.fluxnetworks.api.util.INetworkNBT;
import net.minecraft.util.Direction;

import java.util.Collection;

public interface ITransferHandler extends INetworkNBT {

    /**
     * End server tick
     */
    void tick();

    /**
     * Used for energy transfer
     *
     * @return Buffer
     */
    long getLogicalBuffer();

    long getLogicalRequest();

    /**
     * Used for gui display
     *
     * @return Buffer for point/plug (energy stored for storage)
     */
    default long getEnergyStored() {
        return getLogicalBuffer();
    }

    /**
     * Energy changed last tick, for gui display
     *
     * @return Change
     */
    long getEnergyChange();

    void updateTransfers(Direction... sides);

    Collection<IFluxTransfer> getTransfers();

    long addToNetwork(long maxAmount);

    long removeFromNetwork(long maxAmount, boolean simulate);
}
