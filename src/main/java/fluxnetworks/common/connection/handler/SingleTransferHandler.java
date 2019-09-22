package fluxnetworks.common.connection.handler;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.network.IFluxTransfer;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.api.tileentity.IFluxEnergy;
import fluxnetworks.common.connection.FluxTransferHandler;
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

    /**
     * Discharge Flux Storage
     * @param maxAmount
     * @return
     */
    @Override
    public long addToNetwork(long maxAmount) {
        if(!fluxConnector.isActive()) {
            return 0;
        }
        //long canAdd = Math.min(getConnectorLimit(), maxAmount);
        long add = transfer.addToNetwork(maxAmount, false);
        added += add;
        return add;
    }

    /**
     * Charge Flux Storage or Wireless
     * @param maxAmount
     * @param simulate
     * @return
     */
    @Override
    public long removeFromNetwork(long maxAmount, boolean simulate) {
        if(!fluxConnector.isActive()) {
            return 0;
        }
        long canRemove = Math.min(getConnectorLimit(), maxAmount);
        long remove = transfer.removeFromNetwork(canRemove, simulate);
        if(simulate) {
            request = remove;
        } else {
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
        return Math.min(fluxConnector.getEnergy(), fluxConnector.getCurrentLimit());
    }

    @Override
    public long getEnergyStored() {
        return fluxConnector.getEnergy();
    }
}
