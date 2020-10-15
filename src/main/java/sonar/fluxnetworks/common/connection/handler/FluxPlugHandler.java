package sonar.fluxnetworks.common.connection.handler;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.network.IFluxTransfer;
import sonar.fluxnetworks.common.connection.transfer.SidedTransfers;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;

import java.util.Objects;

public class FluxPlugHandler extends AbstractPlugHandler<TileFluxPlug> {

    public SidedTransfers transfers;
    public long addedExternally;

    public FluxPlugHandler(TileFluxPlug fluxPlug) {
        super(fluxPlug);
        this.transfers = new SidedTransfers(fluxPlug);
    }

    @Override
    public void onStartCycle() {
        super.onStartCycle();
        transfers.getTransfers().stream().filter(Objects::nonNull).forEach(IFluxTransfer::onStartCycle);
    }

    @Override
    public void onEndCycle() {
        super.onEndCycle();
        transfers.getTransfers().stream().filter(Objects::nonNull).forEach(IFluxTransfer::onEndCycle);
        change = addedExternally; ///external additions happen outside the transfer cycle.
        addedExternally = 0;
    }

    @Override
    public long addEnergy(long amount, Direction dir, boolean simulate) {
        if (!getNetwork().isValid()) {
            return 0;
        }
        IFluxTransfer transfer = transfers.getTransfer(dir);
        if (transfer != null) {
            long a = addEnergyToBuffer(amount, simulate);
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
