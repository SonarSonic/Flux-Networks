package sonar.fluxnetworks.common.connection.handler;

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.api.network.IFluxTransfer;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.common.connection.transfer.ConnectionTransfer;
import sonar.fluxnetworks.common.handler.TileEntityHandler;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flux connector(point or plug) transfer handler
 */
public class SidedTransferHandler extends AbstractTransferHandler<IFluxConnector> {

    private final TileFluxCore tileEntity;
    private Map<Direction, IFluxTransfer> transfers = new HashMap<>();

    public SidedTransferHandler(TileFluxCore tile, IFluxConnector fluxConnector) {
        super(fluxConnector);
        this.tileEntity = tile;
    }

    {
        for(Direction facing : Direction.values()) {
            transfers.put(facing, null);
        }
    }

    @Override
    public void onLastEndTick() {
        super.onLastEndTick();
        // Useless this time
        //transfers.values().stream().filter(Objects::nonNull).forEach(IFluxTransfer::onServerStartTick);
    }

    @Override
    public void updateTransfers(Direction... faces) {
        for(Direction dir : faces) {
            TileEntity tile = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(dir));
            setTransfer(dir, tile);
        }
    }

    private void setTransfer(Direction dir, TileEntity tileEntity) {
        IFluxTransfer transfer = transfers.get(dir);
        ITileEnergyHandler handler;
        if(tileEntity == null || (handler = TileEntityHandler.getEnergyHandler(tileEntity, dir.getOpposite())) == null) {
            transfers.put(dir, null);
        } else if (transfer == null || transfer.getTile() != tileEntity) {
            transfers.put(dir, new ConnectionTransfer(this, handler, tileEntity, dir));
        } else if (transfer.isInvalid()) {
            transfers.put(dir, null);
        }
    }

    @Override
    public List<IFluxTransfer> getTransfers() {
        return Lists.newArrayList(transfers.values());
    }

    public long addPhantomEnergyToNetwork(long amount, Direction dir, boolean simulate) {
        if(getNetwork().isInvalid()) {
            return 0;
        }
        IFluxTransfer transfer = transfers.get(dir);
        if(transfer != null) {
            long a = addToBuffer(amount, simulate);
            if(!simulate) {
                transfer.addedToNetwork(a);
            }
            return a;
        }
        return 0;
    }

}
