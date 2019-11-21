package icyllis.fluxnetworks.common.tileentity.component;

import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.api.tile.IFluxTransfer;
import icyllis.fluxnetworks.api.util.ITileEnergyHandler;
import icyllis.fluxnetworks.system.handler.TileEntityHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import java.util.Collection;
import java.util.HashMap;

/**
 * Plug or point transfer handler (6 sides)
 */
public class ConnectionTransferHandler extends FluxTransferHandler {

    private HashMap<Direction, IFluxTransfer> transfers = new HashMap<>(6);

    public ConnectionTransferHandler(IFluxTile tile) {
        super(tile);
        for (Direction side : Direction.values()) {
            transfers.put(side, null);
        }
    }

    @Override
    public void updateTransfers(Direction... sides) {
        for (Direction side : sides) {
            TileEntity target = this.tile.getWorld().getTileEntity(this.tile.getPos().offset(side));
            IFluxTransfer transfer = transfers.get(side);
            ITileEnergyHandler handler = TileEntityHandler.INSTANCE.getEnergyHandler(target, side.getOpposite());
            if (handler == null) {
                transfers.put(side, null);
            } else if (transfer == null || target != transfer.getTile()) {
                transfers.put(side, new ConnectionTransfer(handler, target, side.getOpposite()));
            } else if (transfer.isInvalid()) {
                transfers.put(side, null);
            }
        }
    }

    @Override
    public Collection<IFluxTransfer> getTransfers() {
        return transfers.values();
    }

    /**
     * Only called by flux plug
     */
    public long addPhantomEnergyToNetwork(long amount, Direction side, boolean simulate) {
        if (!tile.getNetwork().isValid()) {
            return 0;
        }
        IFluxTransfer transfer = transfers.get(side);
        if (transfer != null) {
            return addToBuffer(amount, simulate);
        }
        return 0;
    }
}
