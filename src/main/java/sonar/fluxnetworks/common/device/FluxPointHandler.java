package sonar.fluxnetworks.common.device;

public class FluxPointHandler extends FluxConnectorHandler {

    private long mDesired;

    public FluxPointHandler() {
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        mDesired = send(getLimit(), true);
    }

    @Override
    public void onCycleEnd() {
        mBuffer += mChange = -send(Math.min(mBuffer, getLimit()), false);
    }

    @Override
    public void insert(long energy) {
        mBuffer += energy;
    }

    @Override
    public long getRequest() {
        return Math.max(mDesired - mBuffer, 0);
    }

    private long send(long energy, boolean simulate) {
        long leftover = energy;
        for (SideTransfer transfer : mTransfers) {
            if (transfer != null) {
                leftover -= transfer.send(leftover, simulate);
                if (leftover <= 0) {
                    return energy;
                }
            }
        }
        return energy - leftover;
    }
}
