package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.api.tiles.IFluxDevice;

import java.util.Iterator;
import java.util.List;

public class TransferIterator<T extends IFluxDevice> {

    private Iterator<PriorityGroup<T>> groupIterator;
    private PriorityGroup<T> currentGroup;

    private Iterator<T> fluxIterator;
    private T currentFlux;

    private boolean isPoint;
    public boolean finish = false;

    public void reset(List<PriorityGroup<T>> list, boolean isPoint) {
        groupIterator = list.iterator();
        currentGroup = null;
        fluxIterator = null;
        currentFlux = null;
        this.isPoint = isPoint;
        finish = false;
        incrementGroup();
    }

    public boolean incrementGroup() {
        if (groupIterator.hasNext()) {
            currentGroup = groupIterator.next();
            fluxIterator = currentGroup.getConnectors().iterator();
            return incrementFlux();
        }
        finish = true;
        return false;
    }

    public boolean incrementFlux() {
        if (fluxIterator.hasNext()) {
            currentFlux = fluxIterator.next();
            return needTransfer() || incrementFlux();
        }
        return incrementGroup();
    }

    public boolean needTransfer() {
        if (!currentFlux.isActive()) {
            return false;
        }
        if (isPoint) {
            return currentFlux.getTransferHandler().getRequest() > 0;
        } else {
            return currentFlux.getTransferHandler().getBuffer() > 0;
        }
    }

    public T getCurrentFlux() {
        return currentFlux;
    }

    public boolean hasNext() {
        if (finish) {
            return false;
        }
        return needTransfer() || incrementFlux();
    }
}
