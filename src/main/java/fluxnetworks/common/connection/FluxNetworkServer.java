package fluxnetworks.common.connection;

import com.google.common.collect.Lists;
import fluxnetworks.api.MemberPermission;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.api.tileentity.IFluxPlug;
import fluxnetworks.api.tileentity.IFluxPoint;
import fluxnetworks.common.event.FluxConnectionEvent;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Flux Network.
 */
public class FluxNetworkServer extends FluxNetworkBase {

    public HashMap<IFluxConnector.ConnectionType, List<IFluxConnector>> connections = new HashMap<>();
    public Queue<IFluxConnector> toAdd = new ConcurrentLinkedQueue<>();
    public Queue<IFluxConnector> toRemove = new ConcurrentLinkedQueue<>();

    public boolean sortConnections = true;

    public List<PriorityGroup<IFluxPlug>> sortedPlugs = new ArrayList<>();
    public List<PriorityGroup<IFluxPoint>> sortedPoints = new ArrayList<>();

    private TransferIterator<IFluxPlug> plugTransferIterator = new TransferIterator<>();
    private TransferIterator<IFluxPoint> pointTransferIterator = new TransferIterator<>();

    public long bufferLimiter = 0;

    public FluxNetworkServer() {
        super();
    }

    public FluxNetworkServer(int id, String name, SecurityType security, int color, UUID owner, EnergyType energy, String password) {
        super(id, name, security, color, owner, energy, password);
    }

    public void addConnections() {
        if(toAdd.isEmpty()) {
            return;
        }
        Iterator<IFluxConnector> iterator = toAdd.iterator();
        while(iterator.hasNext()) {
            IFluxConnector flux = iterator.next();
            FluxUtils.addWithCheck(getConnections(flux.getConnectionType()), flux);
            MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Connected(flux, this));
            iterator.remove();
            sortConnections = true;
        }
    }

    public void removeConnections() {
        if(toRemove.isEmpty()) {
            return;
        }
        Iterator<IFluxConnector> iterator = toRemove.iterator();
        while(iterator.hasNext()) {
            IFluxConnector flux = iterator.next();
            getConnections(flux.getConnectionType()).removeIf(t -> t == flux);
            MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Disconnected(flux, this));
            iterator.remove();
            sortConnections = true;
        }
    }

    public <T extends IFluxConnector> List<T> getConnections(IFluxConnector.ConnectionType type) {
        return (List<T>) connections.computeIfAbsent(type, m -> Lists.newArrayList());
    }

    @Override
    public void onStartServerTick() {
        List<IFluxPlug> plugs = getConnections(IFluxConnector.ConnectionType.PLUG);
        List<IFluxPoint> points = getConnections(IFluxConnector.ConnectionType.POINT);
        plugs.forEach(f -> f.getTransferHandler().onServerStartTick());
        points.forEach(f -> f.getTransferHandler().onServerStartTick());
    }

    @Override
    public void onEndServerTick() {
        addConnections();
        removeConnections();
        if(sortConnections) {
            sortConnections();
            sortConnections = false;
        }

        bufferLimiter = 0;

        if(!sortedPoints.isEmpty()) {
            sortedPoints.forEach(g -> g.getConnectors().forEach(p -> bufferLimiter += p.getTransferHandler().removeFromNetwork(Integer.MAX_VALUE, true, true)));
            if (!sortedPlugs.isEmpty()) {
                pointTransferIterator.update(sortedPoints, true);
                plugTransferIterator.update(sortedPlugs, false);
                CYCLE:
                while (pointTransferIterator.hasNext()) {
                    while (plugTransferIterator.hasNext()) {
                        IFluxPlug plug = plugTransferIterator.getCurrentFlux();
                        IFluxPoint point = pointTransferIterator.getCurrentFlux();
                        long removed = plug.getTransferHandler().addToNetwork(point.getTransferHandler().getRequest(), true);
                        long added = point.getTransferHandler().removeFromNetwork(removed, true, false);
                        if(added > 0) {
                            long actualRemoved = plug.getTransferHandler().addToNetwork(added, false);
                            point.getTransferHandler().removeFromNetwork(actualRemoved, false, false);
                            if(point.getTransferHandler().getRequest() <= 0) {
                                continue CYCLE;
                            }
                        } else {
                            // EU received 4RF at least, prevent dead loop
                            plugTransferIterator.incrementFlux();
                        }
                    }
                    break CYCLE;
                }
            }
        }

//        List<IFluxPlug> plugs = getConnections(IFluxConnector.ConnectionType.PLUG);
//        List<IFluxPoint> points = getConnections(IFluxConnector.ConnectionType.POINT);
//        plugs.forEach(f -> f.getTransferHandler().onWorldEndTick());
//        points.forEach(f -> f.getTransferHandler().onWorldEndTick());
    }

    @Override
    public MemberPermission getMemberPermission(EntityPlayer player) {
        return network_players.getValue().stream().collect(Collectors.toMap(NetworkMember::getPlayerUUID, p -> p.getPermission())).getOrDefault(EntityPlayer.getUUID(player.getGameProfile()), network_security.getValue().isEncrypted() ? MemberPermission.NONE : MemberPermission.ACCESS);
    }

    @Override
    public void onRemoved() {
        getConnections(IFluxConnector.ConnectionType.POINT).forEach(flux -> MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Disconnected(flux, this)));
        getConnections(IFluxConnector.ConnectionType.PLUG).forEach(flux -> MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Disconnected(flux, this)));
        connections.clear();
        toAdd.clear();
        toRemove.clear();
        sortedPoints.clear();
        sortedPlugs.clear();
    }

    @Override
    public void queueConnectionAddition(IFluxConnector flux) {
        toAdd.add(flux);
    }

    @Override
    public void queueConnectionRemoval(IFluxConnector flux, boolean chunkUnload) {
        toRemove.add(flux);
    }

    private void sortConnections() {
        sortedPlugs.clear();
        sortedPoints.clear();
        List<IFluxPlug> plugs = getConnections(IFluxConnector.ConnectionType.PLUG);
        List<IFluxPoint> points = getConnections(IFluxConnector.ConnectionType.POINT);
        plugs.forEach(p -> PriorityGroup.getOrCreateGroup(p.getPriority(), sortedPlugs).getConnectors().add(p));
        points.forEach(p -> PriorityGroup.getOrCreateGroup(p.getPriority(), sortedPoints).getConnectors().add(p));
        sortedPlugs.sort(Comparator.comparing(p -> -p.getPriority()));
        sortedPoints.sort(Comparator.comparing(p -> -p.getPriority()));
    }
}
