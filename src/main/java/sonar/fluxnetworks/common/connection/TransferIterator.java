package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class TransferIterator implements Iterator<TileFluxDevice> {

    private final boolean mPoint;

    private Iterator<TileFluxDevice> mIterator;
    private TileFluxDevice mNext;

    public TransferIterator(boolean point) {
        mPoint = point;
    }

    public void reset(@Nonnull List<TileFluxDevice> list) {
        mIterator = list.iterator();
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
            return mNext.getTransferNode().getRequest() > 0;
        } else {
            return mNext.getTransferNode().getBuffer() > 0;
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
    public TileFluxDevice next() {
        return mNext;
    }
}
