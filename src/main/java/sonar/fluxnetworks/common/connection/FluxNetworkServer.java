package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.device.IFluxPlug;
import sonar.fluxnetworks.api.device.IFluxPoint;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class handles a single flux Network on logic server.
 */
public class FluxNetworkServer extends BasicFluxNetwork {

    private final Map<FluxLogicType, List<? extends IFluxDevice>> connections = new EnumMap<>(FluxLogicType.class);

    private final Queue<IFluxDevice> toAdd = new LinkedList<>();
    private final Queue<IFluxDevice> toRemove = new LinkedList<>();

    public boolean sortConnections = true;

    // storage can work as both plug and point logically
    private final List<PriorityGroup<IFluxPlug>> sortedPlugs = new ArrayList<>();
    private final List<PriorityGroup<IFluxPoint>> sortedPoints = new ArrayList<>();

    private final TransferIterator<IFluxPlug> plugTransferIterator = new TransferIterator<>(false);
    private final TransferIterator<IFluxPoint> pointTransferIterator = new TransferIterator<>(true);

    private long bufferLimiter = 0;

    public FluxNetworkServer() {

    }

    public FluxNetworkServer(int id, String name, int color, UUID owner) {
        super(id, name, color, owner);
    }

    /*public void addConnections() {
        if (toAdd.isEmpty()) {
            return;
        }
        Iterator<IFluxConnector> iterator = toAdd.iterator();
        while (iterator.hasNext()) {
            IFluxConnector flux = iterator.next();
            FluxCacheType.getValidTypes(flux).forEach(t -> FluxUtils.addWithCheck(getConnections(t), flux));
            MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Connected(flux, this));
            iterator.remove();
            sortConnections = true;
        }
    }

    public void removeConnections() {
        if (toRemove.isEmpty()) {
            return;
        }
        Iterator<IFluxConnector> iterator = toRemove.iterator();
        while (iterator.hasNext()) {
            IFluxConnector flux = iterator.next();
            FluxCacheType.getValidTypes(flux).forEach(t -> getConnections(t).removeIf(f -> f == flux));
            iterator.remove();
            sortConnections = true;
        }
    }*/

    private void handleConnectionQueue() {
        IFluxDevice device;
        while ((device = toAdd.poll()) != null) {
            boolean b = false;
            for (FluxLogicType type : FluxLogicType.getValidTypes(device)) {
                b |= FluxUtils.addWithCheck(getConnections(type), device);
            }
            if (b) {
                //MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.Connected(device, this));
                device.onConnect(this);
                sortConnections = true;
            }
        }
        while ((device = toRemove.poll()) != null) {
            for (FluxLogicType type : FluxLogicType.getValidTypes(device)) {
                sortConnections |= getConnections(type).remove(device);
            }
        }
        if (sortConnections) {
            sortConnections();
            sortConnections = false;
        }
    }

    private void sortConnections() {
        sortedPlugs.clear();
        sortedPoints.clear();
        List<IFluxPlug> plugs = getConnections(FluxLogicType.PLUG);
        List<IFluxPoint> points = getConnections(FluxLogicType.POINT);
        plugs.forEach(p -> PriorityGroup.getOrCreateGroup(p.getLogicPriority(), sortedPlugs).getDevices().add(p));
        points.forEach(p -> PriorityGroup.getOrCreateGroup(p.getLogicPriority(), sortedPoints).getDevices().add(p));
        sortedPlugs.sort(PriorityGroup.DESCENDING_ORDER);
        sortedPoints.sort(PriorityGroup.DESCENDING_ORDER);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IFluxDevice> List<T> getConnections(FluxLogicType type) {
        return (List<T>) connections.computeIfAbsent(type, m -> new ArrayList<>());
    }

    @Override
    public void onEndServerTick() {
        statistics.startProfiling();

        handleConnectionQueue();

        bufferLimiter = 0;

        List<IFluxDevice> devices = getConnections(FluxLogicType.ANY);
        devices.forEach(f -> {
            f.getTransferHandler().onCycleStart();
            bufferLimiter += f.getTransferHandler().getRequest();
        });

        if (!sortedPoints.isEmpty()) {
            if (bufferLimiter > 0 && !sortedPlugs.isEmpty()) {
                plugTransferIterator.reset(sortedPlugs);
                pointTransferIterator.reset(sortedPoints);
                CYCLE:
                while (pointTransferIterator.hasNext()) {
                    while (plugTransferIterator.hasNext()) {
                        IFluxPlug plug = plugTransferIterator.next();
                        IFluxPoint point = pointTransferIterator.next();
                        if (plug.getDeviceType() == point.getDeviceType()) {
                            break CYCLE; // Storage always have the lowest priority, the cycle can be broken here.
                        }
                        long operate = plug.getTransferHandler().removeFromBuffer(point.getTransferHandler().getRequest(), true);
                        long removed = point.getTransferHandler().addToBuffer(operate, false);
                        if (removed > 0) {
                            plug.getTransferHandler().removeFromBuffer(removed, false);
                            if (point.getTransferHandler().getRequest() <= 0) {
                                continue CYCLE;
                            }
                        } else {
                            // If we can only transfer 3RF, it returns 0 (3RF < 1EU), but this plug still need transfer (3RF > 0), and can't afford current point,
                            // So manually increment plug to prevent dead loop. (hasNext detect if it need transfer)
                            if (plug.getTransferHandler().getBuffer() < 4) {
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
        devices.forEach(f -> f.getTransferHandler().onCycleEnd());

        statistics.stopProfiling();
    }

    @Override
    public long getBufferLimiter() {
        return bufferLimiter;
    }

    @Nonnull
    @Override
    public AccessLevel getPlayerAccess(PlayerEntity player) {
        if (FluxConfig.enableSuperAdmin) {
            if (SuperAdmin.isPlayerSuperAdmin(player)) {
                return AccessLevel.SUPER_ADMIN;
            }
        }
        /*return network_players.getValue()
                .stream().collect(Collectors.toMap(NetworkMember::getPlayerUUID, NetworkMember::getAccessPermission))
                .getOrDefault(PlayerEntity.getUUID(player.getGameProfile()),
                        network_security.getValue().isEncrypted() ? EnumAccessType.NONE : EnumAccessType.USER);*/
        UUID uuid = PlayerEntity.getUUID(player.getGameProfile());
        Optional<NetworkMember> member = getMemberByUUID(uuid);
        if (member.isPresent()) {
            return member.get().getPlayerAccess();
        }
        return security.isEncrypted() ? AccessLevel.BLOCKED : AccessLevel.USER;
    }

    @Override
    public void onDeleted() {
        getConnections(FluxLogicType.ANY).forEach(IFluxDevice::onDisconnect);
        connections.clear();
        toAdd.clear();
        toRemove.clear();
        sortedPoints.clear();
        sortedPlugs.clear();
    }

    @Override
    public void enqueueConnectionAddition(@Nonnull IFluxDevice device) {
        if (device instanceof PhantomFluxDevice) {
            throw new IllegalStateException();
        }
        if (getConnections(FluxLogicType.ANY).contains(device)) {
            return;
        }
        device.getNetwork().enqueueConnectionRemoval(device, false);
        if (!toAdd.contains(device)) {
            toAdd.offer(device);
            toRemove.remove(device);
            allConnections.put(device.getGlobalPos(), device);
        }
    }

    @Override
    public void enqueueConnectionRemoval(@Nonnull IFluxDevice device, boolean chunkUnload) {
        if (device instanceof PhantomFluxDevice) {
            throw new IllegalArgumentException();
        }
        if (getConnections(FluxLogicType.ANY).contains(device) && !toRemove.contains(device)) {
            toRemove.offer(device);
            toAdd.remove(device);
            if (chunkUnload) {
                // create a fake device on server side, representing it has ever connected to
                // this network but currently unloaded
                allConnections.put(device.getGlobalPos(), new PhantomFluxDevice(device));
            } else {
                // remove the tile entity
                allConnections.remove(device.getGlobalPos());
            }
        }
    }

    /*private void addToLite(IFluxDevice flux) {
        Optional<IFluxDevice> c = all_connectors.getValue().stream().filter(f -> f.getCoords().equals(flux.getCoords())).findFirst();
        if (c.isPresent()) {
            changeChunkLoaded(flux, true);
        } else {
            SimpleFluxDevice lite = new SimpleFluxDevice(flux);
            all_connectors.getValue().add(lite);
        }
    }

    private void removeFromLite(IFluxDevice flux) {
        all_connectors.getValue().removeIf(f -> f.getCoords().equals(flux.getCoords()));
    }

    private void changeChunkLoaded(IFluxDevice flux, boolean chunkLoaded) {
        Optional<IFluxDevice> c = all_connectors.getValue().stream().filter(f -> f.getCoords().equals(flux.getCoords())).findFirst();
        c.ifPresent(fluxConnector -> fluxConnector.setChunkLoaded(chunkLoaded));
    }

    @Override
    public void addNewMember(String name) {
        NetworkMember a = NetworkMember.createMemberByUsername(name);
        if (network_players.getValue().stream().noneMatch(f -> f.getPlayerUUID().equals(a.getPlayerUUID()))) {
            network_players.getValue().add(a);
        }
    }

    @Override
    public void removeMember(UUID uuid) {
        network_players.getValue().removeIf(p -> p.getPlayerUUID().equals(uuid) && !p.getAccessPermission().canDelete());
    }*/

    @Override
    public Optional<NetworkMember> getMemberByUUID(UUID playerUUID) {
        return memberList.stream().filter(f -> f.getPlayerUUID().equals(playerUUID)).findFirst();
    }

    @Deprecated
    public void markLiteSettingChanged(IFluxDevice flux) {

    }
}
