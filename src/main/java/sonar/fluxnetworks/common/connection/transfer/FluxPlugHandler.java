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

    private long received;
    private long removed;

    public FluxPlugHandler(TileFluxPlug fluxPlug) {
        super(fluxPlug);
    }

    @Override
    public void onCycleStart() {
        for (ConnectionTransfer transfer : transfers.values()) {
            if (transfer != null) {
                transfer.onCycleStart();
            }
        }
    }

    @Override
    public void onCycleEnd() {
        change = received;
        received = 0;
        removed = 0;
    }

    @Override
    public long removeFromBuffer(long energy) {
        long a = Math.min(Math.min(energy, buffer), device.getLogicLimit() - removed);
        if (a <= 0) {
            return 0;
        }
        buffer -= a;
        removed += a;
        return a;
    }

    @Override
    public long receiveFromSupplier(long amount, @Nonnull Direction side, boolean simulate) {
        if (!device.getNetwork().isValid()) {
            return 0;
        }
        ConnectionTransfer transfer = transfers.get(side);
        if (transfer != null) {
            long a = device.getNetwork().getBufferLimiter() - buffer;
            if (a <= 0) {
                return 0;
            }
            a = Math.min(amount, Math.min(a, device.getLogicLimit()) - buffer);
            if (a <= 0) {
                return 0;
            }
            if (!simulate) {
                buffer += a;
                received += a; // external additions happen outside the transfer cycle.
                transfer.onEnergyReceived(a);
            }
            return a;
        }
        return 0;
    }

    @Override
    public void updateTransfers(@Nonnull Direction... faces) {
        updateSidedTransfers(device.getFluxWorld(), device.getPos(), transfers, faces);
    }

    static void updateSidedTransfers(World world, BlockPos pos, Map<Direction, ConnectionTransfer> transfers, @Nonnull Direction[] faces) {
        for (Direction dir : faces) {
            TileEntity tile = world.getTileEntity(pos.offset(dir));
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
