package sonar.fluxnetworks.common.device;

public class FluxPointHandler extends FluxSidedHandler {

    private long mDesired;

    public FluxPointHandler() {
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        mDesired = sendToConsumers(getLimit(), true);
    }

    @Override
    public void onCycleEnd() {
        mBuffer += mChange = -sendToConsumers(Math.min(mBuffer, getLimit()), false);
    }

    @Override
    public void addToBuffer(long energy) {
        mBuffer += energy;
    }

    @Override
    public long getRequest() {
        return Math.max(mDesired - mBuffer, 0);
    }

    private long sendToConsumers(long energy, boolean simulate) {
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
