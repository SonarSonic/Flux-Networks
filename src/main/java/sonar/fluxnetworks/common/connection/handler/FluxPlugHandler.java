package sonar.fluxnetworks.common.connection.handler;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.network.IFluxTransfer;
import sonar.fluxnetworks.common.connection.transfer.SidedTransfers;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FluxPlugHandler extends BasicTransferHandler<TileFluxPlug> {

    public SidedTransfers transfers;
    public long addedExternally;

    public FluxPlugHandler(TileFluxPlug fluxPlug) {
        super(fluxPlug);
        this.transfers = new SidedTransfers(fluxPlug);
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        transfers.getTransfers().stream().filter(Objects::nonNull).forEach(IFluxTransfer::onStartCycle);
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        transfers.getTransfers().stream().filter(Objects::nonNull).forEach(IFluxTransfer::onEndCycle);
        change = addedExternally; ///external additions happen outside the transfer cycle.
        addedExternally = 0;
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
        IFluxTransfer transfer = transfers.getTransfer(side);
        if (transfer != null) {
            long a = addToBuffer(amount, simulate);
            if (!simulate) {
                transfer.onEnergyRemoved(a);
                addedExternally += a;
            }
            return a;
        }
        return 0;
    }

    @Override
    public void updateTransfers(Direction... faces) {
        transfers.updateTransfers(faces);
    }
}
