package icyllis.fluxnetworks.api.network;

public interface INetworkTransfer {

    long getBufferLimiter();

    void markToSort();

    void tick();

    void onRemoved();
}
