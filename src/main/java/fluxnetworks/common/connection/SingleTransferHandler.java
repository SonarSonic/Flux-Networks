package fluxnetworks.common.connection;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.network.IFluxTransfer;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.api.tileentity.IFluxEnergy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class SingleTransferHandler extends FluxTransferHandler<IFluxEnergy> {

    public final IFluxTransfer transfer;

    public SingleTransferHandler(IFluxEnergy tile, IFluxTransfer transfer) {
        super(tile);
        this.transfer = transfer;
    }

    @Override
    public void onServerStartTick() {
        super.onServerStartTick();
        transfer.onServerStartTick();
    }

    @Override
    public long addToNetwork(long maxAmount, boolean simulate) {
        if(!fluxConnector.isActive()) {
            return 0;
        }
        long canAdd = Math.min(getConnectorLimit(), maxAmount);
        long add = transfer.addToNetwork(canAdd, simulate);
        if(!simulate) {
            added += add;
        }
        return add;
    }

    /**
     * Charge Flux Storage or Wireless
     * @param maxAmount
     * @param simulate
     * @param pre
     * @return
     */
    @Override
    public long removeFromNetwork(long maxAmount, boolean simulate, boolean pre) {
        if(!fluxConnector.isActive()) {
            return 0;
        }
        long canRemove = Math.min(getConnectorLimit(), maxAmount);
        long remove = transfer.removeFromNetwork(canRemove, simulate);
        if(pre) {
            request += remove;
        }
        if(!simulate) {
            request -= remove;
            removed += remove;
        }
        return remove;
    }

    @Override
    public void updateTransfers(EnumFacing... faces) {

    }

    @Override
    public List<IFluxTransfer> getTransfers() {
        return Lists.newArrayList(transfer);
    }

    @Override
    public long getBuffer() {
        return fluxConnector.getEnergy();
    }
}
