package sonar.fluxnetworks.common.connection.transfer;

import sonar.fluxnetworks.api.device.IFluxPoint;

public abstract class BasicPointHandler<T extends IFluxPoint> extends BasicTransferHandler<T> {

    private long request;

    public BasicPointHandler(T device) {
        super(device);
    }

    @Override
    public void onCycleStart() {
        super.onCycleStart();
        request = sendToConsumers(getRemoveLimit(), true);
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        buffer += change = -sendToConsumers(buffer, false);
    }

    @Override
    public long getRequest(){
        return request - addedToBuffer;
    }

    @Override
    public long getAddLimit() {
        return Math.min(request, device.getLogicLimit());
    }

    protected abstract long sendToConsumers(long energy, boolean simulate);
}
