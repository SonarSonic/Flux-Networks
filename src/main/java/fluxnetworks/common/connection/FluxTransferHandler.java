package fluxnetworks.common.connection;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.IFluxTransfer;
import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.api.tileentity.IFluxConnector;
import net.minecraft.nbt.NBTTagCompound;

public abstract class FluxTransferHandler<T extends IFluxConnector> implements ITransferHandler {

    public final T fluxConnector;

    public long added;
    public long removed;

    public long change;

    public long buffer;
    public long request;

    private long bufferSize;

    public FluxTransferHandler(T fluxConnector) {
        this.fluxConnector = fluxConnector;
    }

    public IFluxNetwork getNetwork() {
        return fluxConnector.getNetwork();
    }

    /**
     * Flux Plug
     * @param maxAmount
     * @return
     */
    @Override
    public long addToNetwork(long maxAmount) {
        if(!fluxConnector.isActive()) {
            return 0;
        }
        //long added = Math.min(maxAmount, buffer);
        buffer -= maxAmount;
        return maxAmount;
    }

    /**
     * Flux point
     * @param maxAmount
     * @param simulate
     * @return
     */
    @Override
    public long removeFromNetwork(long maxAmount, boolean simulate) {
        if(!fluxConnector.isActive())
            return 0;
        long canRemove = getValidRemoval(maxAmount);
        long removed = 0;
        for(IFluxTransfer transfer : getTransfers()) {
            if(transfer != null) {
                long toTransfer = canRemove - removed;
                long remove = transfer.removeFromNetwork(toTransfer, simulate);
                removed += remove;
                if(!simulate) {
                    this.removed += remove;
                    request -= remove;
                }
            }
        }
        if(simulate) {
            request = removed;
        }
        return removed;
    }

    public long getConnectorLimit() {
        return fluxConnector.getCurrentLimit();
    }

    public long getBufferLimiter() {
        return ((FluxNetworkServer) getNetwork()).bufferLimiter;
    }

    @Override
    public void onServerStartTick() {
        change = added - removed;
        request = 0;
        added = 0;
        removed = 0;
    }

    public long addToBuffer(long add, boolean simulate) {
        long r = getValidAddition(add);
        if(r > 0) {
            if(!simulate) {
                buffer += r;
                added += r;
            }
            return r;
        }
        return 0;
    }

    @Override
    public long getChange() {
        return change;
    }

    @Override
    public long getBuffer() {
        return buffer;
    }

    @Override
    public long getRequest() {
        return request;
    }

    private void checkBufferSize() {
        bufferSize = Math.min(Math.max(bufferSize, getConnectorLimit()), getBufferLimiter());
    }

    private long getMaxAddition() {
        long r = Math.min(getConnectorLimit() - added, bufferSize - buffer);
        return r > 0 ? r : 0 ;
    }

    private long getMaxRemoval() {
        return getConnectorLimit() - removed;
    }

    private long getValidAddition(long toAdd) {
        checkBufferSize();
        return Math.min(getMaxAddition(), toAdd);
    }

    private long getValidRemoval(long toRemove) {
        return Math.min(getMaxRemoval(), toRemove);
    }

    /**
     * Send data to client tile entity for gui, this is always last tick energy change.
     */
    public NBTTagCompound writeNetworkedNBT(NBTTagCompound tag) {
        tag.setLong("71", change);
        return tag;
    }

    public void readNetworkedNBT(NBTTagCompound tag) {
        change = tag.getLong("71");
    }
}
