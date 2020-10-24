package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.common.tileentity.TileFluxPoint;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public class FluxPointHandler extends BasicPointHandler<TileFluxPoint> {

    private final Map<Direction, ConnectionTransfer> transfers = new EnumMap<>(Direction.class);

    public FluxPointHandler(TileFluxPoint fluxPoint) {
        super(fluxPoint);
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
    public long sendToConsumers(long energy, boolean simulate) {
        if (!device.isActive()) {
            return 0;
        }
        long leftover = energy;
        for (ConnectionTransfer transfer : transfers.values()) {
            if (transfer != null) {
                leftover -= transfer.sendToTile(leftover, simulate);
                if (leftover <= 0) {
                    return energy;
                }
            }
        }
        return energy - leftover;
    }

    @Override
    public void updateTransfers(@Nonnull Direction... faces) {
        FluxPlugHandler.updateSidedTransfers(device.getFluxWorld(), device.getPos(), transfers, faces);
    }
}
