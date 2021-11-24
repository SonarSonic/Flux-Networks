package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.common.blockentity.FluxDeviceEntity;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class TransferIterator implements Iterator<FluxDeviceEntity> {

    private final boolean mPoint;

    private Iterator<FluxDeviceEntity> mIterator;
    private FluxDeviceEntity mNext;

    public TransferIterator(boolean point) {
        mPoint = point;
    }

    public void reset(@Nonnull List<FluxDeviceEntity> list) {
        mIterator = list.listIterator();
        if (mIterator.hasNext()) {
            mNext = mIterator.next();
        } else {
            mNext = null;
        }
    }

    public boolean increment() {
        if (mIterator.hasNext()) {
            mNext = mIterator.next();
            return needTransfer() || increment();
        }
        mNext = null;
        return false;
    }

    private boolean needTransfer() {
        /*if (!mNext.isActive()) {
            return false;
        }*/
        if (mPoint) {
            return mNext.getTransferHandler().getRequest() > 0;
        } else {
            return mNext.getTransferHandler().getBuffer() > 0;
        }
    }

    @Override
    public boolean hasNext() {
        if (mNext == null) {
            return false;
        }
        return needTransfer() || increment();
    }

    @Override
    public FluxDeviceEntity next() {
        return mNext;
    }
}
