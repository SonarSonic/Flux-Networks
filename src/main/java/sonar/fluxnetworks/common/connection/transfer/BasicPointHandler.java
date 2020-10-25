package sonar.fluxnetworks.common.connection.transfer;

import sonar.fluxnetworks.api.device.IFluxPoint;

public abstract class BasicPointHandler<T extends IFluxPoint> extends BasicTransferHandler<T> {

    private long demand;

    public BasicPointHandler(T device) {
        super(device);
    }

    @Override
    public void onCycleStart() {
        demand = sendToConsumers(device.getLogicLimit(), true);
    }

    @Override
    public void onCycleEnd() {
        buffer += change = -sendToConsumers(Math.min(buffer, device.getLogicLimit()), false);
    }

    @Override
    public void addToBuffer(long energy) {
        if (energy <= 0) {
            return;
        }
        buffer += energy;
    }

    @Override
    public long getRequest() {
        return Math.max(demand - buffer, 0);
    }

    protected abstract long sendToConsumers(long energy, boolean simulate);
}
