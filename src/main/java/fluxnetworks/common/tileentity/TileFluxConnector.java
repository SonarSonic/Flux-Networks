package fluxnetworks.common.tileentity;

import fluxnetworks.api.network.ITransferHandler;
import fluxnetworks.common.connection.handler.ConnectionTransferHandler;
import fluxnetworks.common.handler.TileEntityHandler;
import fluxnetworks.common.tileentity.energy.TileGTEnergy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public abstract class TileFluxConnector extends TileGTEnergy {

    public final ConnectionTransferHandler handler = new ConnectionTransferHandler(this, this);

    @Override
    public long addPhantomEnergyToNetwork(EnumFacing side, long amount, boolean simulate) {
        return isActive() && getConnectionType().canAddEnergy() ? handler.addPhantomEnergyToNetwork(amount, side, simulate) : 0;
    }

    @Override
    public void updateTransfers(EnumFacing... facings) {
        super.updateTransfers(facings);
        boolean sendUpdate = false;
        for(EnumFacing facing : facings) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            boolean b = connections[facing.getIndex()] != 0;
            boolean c = TileEntityHandler.canRenderConnection(tile, facing.getOpposite());
            if(b != c) {
                connections[facing.getIndex()] = (byte) (c ? 1 : 0);
                sendUpdate = true;
            }
        }
        if(sendUpdate) {
            sendPackets();
        }
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }
}
