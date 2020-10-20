package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.common.misc.EnergyUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public class FluxPlugHandler extends BasicTransferHandler<TileFluxPlug> {

    private final Map<Direction, ConnectionTransfer> transfers = new EnumMap<>(Direction.class);

    public FluxPlugHandler(TileFluxPlug fluxPlug) {
        super(fluxPlug);
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        for (ConnectionTransfer transfer : transfers.values()) {
            if (transfer != null) {
                transfer.onCycleStart();
            }
        }
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        for (ConnectionTransfer transfer : transfers.values()) {
            if (transfer != null) {
                transfer.onCycleEnd();
            }
        }
    }

    @Override
    public long getAddLimit() {
        return Math.min(device.getNetwork().getBufferLimiter() - buffer, device.getLogicLimit());
    }

    @Override
    public long receiveFromAdjacency(long amount, @Nonnull Direction side, boolean simulate) {
        if (!device.getNetwork().isValid()) {
            return 0;
        }
        ConnectionTransfer transfer = transfers.get(side);
        if (transfer != null) {
            long a = addToBuffer(amount, simulate);
            if (!simulate) {
                transfer.onEnergyReceived(a);
                change += a; // external additions happen outside the transfer cycle.
            }
            return a;
        }
        return 0;
    }

    @Override
    public void updateTransfers(@Nonnull Direction... faces) {
        updateSidedTransfers(device.getFluxWorld(), device.getPos(), transfers, faces);
    }

    static void updateSidedTransfers(World fluxWorld, BlockPos pos, Map<Direction, ConnectionTransfer> transfers, @Nonnull Direction[] faces) {
        for (Direction dir : faces) {
            TileEntity tile = fluxWorld.getTileEntity(pos.offset(dir));
            ConnectionTransfer transfer = transfers.get(dir);
            ITileEnergyHandler handler;
            if (tile == null || (handler = EnergyUtils.getEnergyHandler(tile, dir.getOpposite())) == null) {
                transfers.put(dir, null);
            } else if (transfer == null || transfer.getTile() != tile) {
                transfers.put(dir, new ConnectionTransfer(handler, tile, dir));
            } else if (transfer.getTile().isRemoved()) {
                transfers.put(dir, null);
            }
        }
    }
}
