package sonar.fluxnetworks.common.connection.handler;

import sonar.fluxnetworks.api.tiles.IFluxConnector;

public abstract class AbstractPointHandler<C extends IFluxConnector> extends AbstractTransferHandler<C> {

    protected long request;

    public AbstractPointHandler(C fluxConnector) {
        super(fluxConnector);
    }

    @Override
    public void onStartCycle() {
        super.onStartCycle();
        request = removeEnergy(getRemoveLimit(), true);
    }

    @Override
    public void onEndCycle() {
        super.onEndCycle();
        buffer += change = -removeEnergy(buffer, false);
    }

    @Override
    public long getRequest(){
        return request - addedToBuffer;
    }

    @Override
    public long getAddLimit() {
        return Math.min(request, fluxConnector.getCurrentLimit());
    }

    public abstract long removeEnergy(long energy, boolean simulate);


}
