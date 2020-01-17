package icyllis.fluxnetworks.fluxnet.manager;

import icyllis.fluxnetworks.api.network.FluxCacheTypes;
import icyllis.fluxnetworks.api.network.FluxConnectionEvent;
import icyllis.fluxnetworks.api.network.IFluxNetwork;
import icyllis.fluxnetworks.api.network.IRequestHandler;
import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.system.util.FluxUtils;
import net.minecraftforge.common.MinecraftForge;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestHandler implements IRequestHandler {

    private final IFluxNetwork network;

    public RequestHandler(IFluxNetwork network) {
        this.network = network;
    }

    public Queue<IFluxTile> queuedAdd = new ConcurrentLinkedQueue<>();
    public Queue<IFluxTile> queuedRemove = new ConcurrentLinkedQueue<>();

    @Override
    public void tick() {
        addConnections();
        removeConnections();
    }

    @Override
    public void onRemoved() {
        queuedAdd.clear();
        queuedRemove.clear();
    }

    @SuppressWarnings("unchecked")
    private void addConnections() {
        if(queuedAdd.isEmpty()) {
            return;
        }
        Iterator<IFluxTile> iterator = queuedAdd.iterator();
        while(iterator.hasNext()) {
            IFluxTile flux = iterator.next();
            FluxCacheTypes.getValidTypes(flux).forEach(t -> FluxUtils.addWithCheck(network.getConnections(t), flux));
            MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Connected(flux, network));
            iterator.remove();
            network.getNetworkTransfer().markToSort();
        }
    }

    @SuppressWarnings("unchecked")
    private void removeConnections() {
        if(queuedRemove.isEmpty()) {
            return;
        }
        Iterator<IFluxTile> iterator = queuedRemove.iterator();
        while(iterator.hasNext()) {
            IFluxTile flux = iterator.next();
            FluxCacheTypes.getValidTypes(flux).forEach(t -> ((List<IFluxTile>) network.getConnections(t)).removeIf(f -> f == flux));
            iterator.remove();
            network.getNetworkTransfer().markToSort();
        }
    }

    @Override
    public void queueConnectionAddition(IFluxTile tile) {
        queuedAdd.add(tile);
        queuedRemove.remove(tile);
    }

    @Override
    public void queueConnectionRemoval(IFluxTile tile, boolean isChunkUnload) {
        queuedRemove.add(tile);
        queuedAdd.remove(tile);
    }
}
