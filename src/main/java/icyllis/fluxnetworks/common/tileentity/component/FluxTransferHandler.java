package icyllis.fluxnetworks.common.tileentity.component;

import icyllis.fluxnetworks.api.tile.IFluxTransfer;
import icyllis.fluxnetworks.api.tile.ITransferHandler;
import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.api.util.NBTType;
import net.minecraft.nbt.CompoundNBT;

public abstract class FluxTransferHandler implements ITransferHandler {

    protected final IFluxTile tile;

    private long added;
    private long removed;

    private long change;

    private long buffer;
    private long request;

    private long bufferSize;

    FluxTransferHandler(IFluxTile tile) {
        this.tile = tile;
    }

    @Override
    public long addToNetwork(long maxAmount) {
        buffer -= maxAmount;
        return maxAmount;
    }

    @Override
    public long removeFromNetwork(long maxAmount, boolean simulate) {
        long canRemove = getValidRemoval(maxAmount);
        long removed = 0;
        for(IFluxTransfer transfer : getTransfers()) {
            if(transfer != null) {
                long to = canRemove - removed;
                long remove = transfer.removeFromNetwork(to, simulate);
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

    @Override
    public void tick() {
        change = Math.abs(added - removed);
        added = 0;
        removed = 0;
        request = 0;
    }

    @Override
    public long getLogicalBuffer() {
        return buffer;
    }

    @Override
    public long getLogicalRequest() {
        return request;
    }

    @Override
    public long getEnergyChange() {
        return change;
    }

    protected long addToBuffer(long add, boolean simulate) {
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

    private void checkBufferSize() {
        bufferSize = Math.min(Math.max(bufferSize, tile.getLogicalLimit()), tile.getNetwork().getTransfer().getBufferLimiter());
    }

    private long getMaxAddition() {
        long r = Math.min(tile.getLogicalLimit() - added, bufferSize - buffer);
        return Math.max(r, 0);
    }

    private long getMaxRemoval() {
        return tile.getLogicalLimit() - removed;
    }

    private long getValidAddition(long toAdd) {
        checkBufferSize();
        return Math.min(getMaxAddition(), toAdd);
    }

    private long getValidRemoval(long toRemove) {
        return Math.min(getMaxRemoval(), toRemove);
    }

    @Override
    public CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type) {
        nbt.putLong("71", change);
        return nbt;
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt, NBTType type) {
        change = nbt.getLong("71");
    }
}
