package sonar.fluxnetworks.common.connection;

import sonar.fluxnetworks.common.device.FluxDeviceEntity;

/**
 * A transfer node is associated with a logical entity in a network.
 *
 * @see FluxDeviceEntity#getTransferNode()
 */
public abstract class TransferNode {

    /**
     * Called before the start of the internal transfer cycle.
     * In this time, external energy transfer should be simulated.
     */
    protected abstract void onCycleStart();

    /**
     * Called after the end of the internal transfer cycle.
     * In this time, external energy transfer should be performed.
     */
    protected abstract void onCycleEnd();

    /**
     * Insert energy to the internal buffer.
     *
     * @param energy the amount
     */
    protected void insert(long energy) {
        throw new UnsupportedOperationException();
    }

    /**
     * Extract energy from the internal buffer.
     *
     * @param energy the desired amount
     * @return the operational amount
     */
    protected long extract(long energy) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the internal buffer of this node
     */
    protected abstract long getBuffer();

    /**
     * @return the required energy of this node
     */
    protected abstract long getRequest();

    /**
     * @return the general priority across the target network
     */
    protected abstract int getPriority();
}
