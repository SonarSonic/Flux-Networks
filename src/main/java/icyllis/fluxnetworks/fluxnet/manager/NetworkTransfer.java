package icyllis.fluxnetworks.fluxnet.manager;

import icyllis.fluxnetworks.api.network.FluxCacheTypes;
import icyllis.fluxnetworks.api.network.IFluxNetwork;
import icyllis.fluxnetworks.api.network.INetworkTransfer;
import icyllis.fluxnetworks.api.tile.IFluxPlug;
import icyllis.fluxnetworks.api.tile.IFluxPoint;
import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.fluxnet.component.PriorityGroup;
import icyllis.fluxnetworks.fluxnet.component.TransferIterator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NetworkTransfer implements INetworkTransfer {

    private final IFluxNetwork network;

    public NetworkTransfer(IFluxNetwork network) {
        this.network = network;
    }

    private boolean sortConnections = true;

    private List<PriorityGroup<IFluxPlug>> sortedPlugs = new ArrayList<>();
    private List<PriorityGroup<IFluxPoint>> sortedPoints = new ArrayList<>();

    private long bufferLimiter = 0;

    private TransferIterator<IFluxPlug> plugTransferIterator = new TransferIterator<>();
    private TransferIterator<IFluxPoint> pointTransferIterator = new TransferIterator<>();

    @Override
    public long getBufferLimiter() {
        return bufferLimiter;
    }

    @Override
    public void markToSort() {
        sortConnections = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void tick() {
        if(sortConnections) {
            sortConnections();
            sortConnections = false;
        }

        bufferLimiter = 0;

        if(!sortedPoints.isEmpty()) {
            sortedPoints.forEach(g -> g.getConnectors().forEach(p -> bufferLimiter += p.getTransferHandler().removeFromNetwork(Long.MAX_VALUE, true)));
            if (bufferLimiter > 0 && !sortedPlugs.isEmpty()) {
                pointTransferIterator.reset(sortedPoints, true);
                plugTransferIterator.reset(sortedPlugs, false);
                CYCLE:
                while (pointTransferIterator.hasNext()) {
                    while (plugTransferIterator.hasNext()) {
                        IFluxPlug plug = plugTransferIterator.getCurrentFlux();
                        IFluxPoint point = pointTransferIterator.getCurrentFlux();
                        if(plug.getConnectionType() == point.getConnectionType()) {
                            break CYCLE; // Storage always have the lowest priority, the cycle can be broken here.
                        }
                        long operate = Math.min(plug.getTransferHandler().getLogicalBuffer(), point.getTransferHandler().getLogicalRequest());
                        long removed = point.getTransferHandler().removeFromNetwork(operate, false);
                        if(removed > 0) {
                            plug.getTransferHandler().addToNetwork(removed);
                            if (point.getTransferHandler().getLogicalRequest() <= 0) {
                                continue CYCLE;
                            }
                        } else {
                            // If we can only transfer 3RF, it returns 0 (3RF < 1EU), but this plug still need transfer (3RF > 0), and can't afford current point,
                            // So manually increment plug to prevent dead loop. (hasNext detect if it need transfer)
                            if(plug.getTransferHandler().getLogicalBuffer() < 4) {
                                plugTransferIterator.incrementFlux();
                            } else {
                                pointTransferIterator.incrementFlux();
                                continue CYCLE;
                            }
                        }
                    }
                    break; //all plugs have been used
                }
            }
        }

        List<IFluxTile> fluxConnectors = network.getConnections(FluxCacheTypes.flux);
        fluxConnectors.forEach(fluxConnector -> fluxConnector.getTransferHandler().onLastEndTick());
    }

    @Override
    public void onRemoved() {
        sortedPlugs.clear();
        sortedPoints.clear();
    }

    @SuppressWarnings("unchecked")
    private void sortConnections() {
        sortedPlugs.clear();
        sortedPoints.clear();
        List<IFluxPlug> plugs = network.getConnections(FluxCacheTypes.plug);
        List<IFluxPoint> points = network.getConnections(FluxCacheTypes.point);
        plugs.forEach(p -> PriorityGroup.getOrCreateGroup(p.getLogicalPriority(), sortedPlugs).getConnectors().add(p));
        points.forEach(p -> PriorityGroup.getOrCreateGroup(p.getLogicalPriority(), sortedPoints).getConnectors().add(p));
        sortedPlugs.sort(Comparator.comparing(p -> -p.getPriority()));
        sortedPoints.sort(Comparator.comparing(p -> -p.getPriority()));
    }
}
