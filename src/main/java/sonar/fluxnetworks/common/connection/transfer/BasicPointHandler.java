package sonar.fluxnetworks.common.connection.transfer;

import sonar.fluxnetworks.api.device.IFluxPoint;

public abstract class BasicPointHandler<T extends IFluxPoint> extends BasicTransferHandler<T> {

    private long allRequest;

    public BasicPointHandler(T device) {
        super(device);
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        allRequest = sendToConsumers(getRemoveLimit(), true);
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        buffer += change = -sendToConsumers(buffer, false);
    }

    @Override
    public long getRequest() {
        return allRequest - addedToBuffer;
    }

    @Override
    public long getAddLimit() {
        return Math.min(allRequest, device.getLogicLimit());
    }

    protected abstract long sendToConsumers(long energy, boolean simulate);
}
