package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.api.device.IFluxDevice;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class TransferIterator<T extends IFluxDevice> {

    private Iterator<PriorityGroup<T>> groupIterator;

    private PriorityGroup<T> currentGroup;

    private Iterator<T> fluxIterator;

    private T currentFlux;

    private boolean finish;

    private final boolean isPoint;

    public TransferIterator(boolean isPoint) {
        this.isPoint = isPoint;
    }

    public void reset(@Nonnull List<PriorityGroup<T>> list) {
        groupIterator = list.iterator();
        currentGroup = null;
        fluxIterator = null;
        currentFlux = null;
        finish = false;
        incrementGroup();
    }

    private boolean incrementGroup() {
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

    private boolean needTransfer() {
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
