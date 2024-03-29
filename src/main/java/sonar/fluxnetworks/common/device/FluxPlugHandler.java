package sonar.fluxnetworks.common.device;

import net.minecraft.core.Direction;

import javax.annotation.Nonnull;

public class FluxPlugHandler extends FluxConnectorHandler {

    // external received energy happen outside the transfer cycle
    private long mReceived;

    // internal removed energy happen inside the transfer cycle
    private long mRemoved;

    public FluxPlugHandler() {
    }

    @Override
    public void onCycleEnd() {
        mChange = mReceived;
        mReceived = 0;
        mRemoved = 0;
    }

    @Override
    public long removeFromBuffer(long energy) {
        long op = Math.min(Math.min(energy, mBuffer), getLimit() - mRemoved);
        assert op >= 0;
        mBuffer -= op;
        mRemoved += op;
        return op;
    }

    public long receive(long maxReceive, @Nonnull Direction side, boolean simulate, long bufferLimiter) {
        long op = Math.min(Math.min(getLimit(), bufferLimiter - mBuffer) - mBuffer, maxReceive);
        if (op > 0) {
            if (!simulate) {
                mBuffer += op;
                mReceived += op;
                SideTransfer transfer = mTransfers[side.get3DDataValue()];
                if (transfer != null) {
                    transfer.receive(op);
                }
            }
            return op;
        }
        return 0;
    }
}
