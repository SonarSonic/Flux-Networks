package sonar.fluxnetworks.common.connection;

import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.*;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class handles a single flux Network on logical server side.
 */
public class FluxNetworkServer extends FluxNetwork {

    private static final Comparator<TileFluxDevice> DESCENDING_ORDER =
            (a, b) -> Integer.compare(b.getTransferNode().getPriority(), a.getTransferNode().getPriority());

    private static final Consumer<TileFluxDevice> DISCONNECT = d -> d.connect(FluxNetworkInvalid.INSTANCE);

    private static final Class<?>[] LOGICAL_TYPES =
            {IFluxDevice.class, IFluxPlug.class, IFluxPoint.class, IFluxStorage.class, IFluxController.class};

    private final List<List<TileFluxDevice>> mDevices = new ArrayList<>(LOGICAL_TYPES.length);

    private final Queue<TileFluxDevice> mToAdd = new LinkedList<>();
    private final Queue<TileFluxDevice> mToRemove = new LinkedList<>();

    private boolean mSortConnections = true;

    private final TransferIterator mPlugTransferIterator = new TransferIterator(false);
    private final TransferIterator mPointTransferIterator = new TransferIterator(true);

    private long mBufferLimiter = 0;

    {
        for (int i = 0; i < LOGICAL_TYPES.length; i++) {
            mDevices.add(new ArrayList<>());
        }
    }

    public FluxNetworkServer() {
    }

    public FluxNetworkServer(int id, @Nonnull String name, int color, @Nonnull Player owner) {
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
        TileFluxDevice device;
        while ((device = mToAdd.poll()) != null) {
            for (int i = 0; i < LOGICAL_TYPES.length; i++) {
                if (LOGICAL_TYPES[i].isInstance(device)) {
                    mSortConnections |= FluxUtils.addWithCheck(getLogicalEntities(i), device);
                }
            }
        }
        while ((device = mToRemove.poll()) != null) {
            for (int i = 0; i < LOGICAL_TYPES.length; i++) {
                if (LOGICAL_TYPES[i].isInstance(device)) {
                    mSortConnections |= getLogicalEntities(i).remove(device);
                }
            }
        }
        if (mSortConnections) {
            getLogicalEntities(PLUG).sort(DESCENDING_ORDER);
            getLogicalEntities(POINT).sort(DESCENDING_ORDER);
            mSortConnections = false;
        }
    }

    @Nonnull
    @Override
    public List<TileFluxDevice> getLogicalEntities(int logic) {
        return mDevices.get(logic);
    }

    @Override
    public void onEndServerTick() {
        mStatistics.startProfiling();

        handleConnectionQueue();

        mBufferLimiter = 0;

        List<TileFluxDevice> devices = getLogicalEntities(ANY);
        for (var f : devices) {
            f.getTransferNode().onCycleStart();
        }

        List<TileFluxDevice> plugs = getLogicalEntities(PLUG);
        List<TileFluxDevice> points = getLogicalEntities(POINT);
        if (!points.isEmpty() && !plugs.isEmpty()) {
            mPlugTransferIterator.reset(plugs);
            mPointTransferIterator.reset(points);
            CYCLE:
            while (mPointTransferIterator.hasNext()) {
                while (mPlugTransferIterator.hasNext()) {
                    TileFluxDevice plug = mPlugTransferIterator.next();
                    TileFluxDevice point = mPointTransferIterator.next();
                    if (plug.getDeviceType() == point.getDeviceType()) {
                        break CYCLE; // Storage always have the lowest priority, the cycle can be broken here.
                    }
                    // we don't need to simulate this action
                    long op = plug.getTransferNode().extract(point.getTransferNode().getRequest());
                    if (op > 0) {
                        point.getTransferNode().insert(op);
                        continue CYCLE;
                    } else {
                        // although the plug still need transfer (buffer > 0)
                        // but it reached max transfer limit, so we use next plug
                        mPlugTransferIterator.increment();
                    }
                }
                break; // all plugs have been used
            }
        }

        for (var f : devices) {
            f.getTransferNode().onCycleEnd();
            mBufferLimiter += f.getTransferNode().getRequest();
        }

        mStatistics.stopProfiling();
    }

    @Override
    public long getBufferLimiter() {
        return mBufferLimiter;
    }

    @Nonnull
    @Override
    public AccessLevel getPlayerAccess(@Nonnull Player player) {
        if (FluxConfig.enableSuperAdmin) {
            if (FluxPlayer.isPlayerSuperAdmin(player)) {
                return AccessLevel.SUPER_ADMIN;
            }
        }
        /*return network_players.getValue()
                .stream().collect(Collectors.toMap(NetworkMember::getPlayerUUID, NetworkMember::getAccessPermission))
                .getOrDefault(PlayerEntity.getUUID(player.getGameProfile()),
                        network_security.getValue().isEncrypted() ? EnumAccessType.NONE : EnumAccessType.USER);*/
        Optional<NetworkMember> member = getMemberByUUID(player.getUUID());
        if (member.isPresent()) {
            return member.get().getAccessLevel();
        }
        return mSecurity.isEncrypted() ? AccessLevel.BLOCKED : AccessLevel.USER;
    }

    @Override
    public void onDelete() {
        super.onDelete();
        getLogicalEntities(ANY).forEach(DISCONNECT);
        mDevices.clear();
        mToAdd.clear();
        mToRemove.clear();
    }

    @Override
    public boolean enqueueConnectionAddition(@Nonnull TileFluxDevice device) {
        if (device.getDeviceType().isController() && getLogicalEntities(CONTROLLER).size() > 0) {
            return false;
        }
        if (!mToAdd.contains(device) && !getLogicalEntities(ANY).contains(device)) {
            mToAdd.offer(device);
            mToRemove.remove(device);
            mConnections.put(device.getGlobalPos(), device);
            return true;
        }
        return false;
    }

    @Override
    public void enqueueConnectionRemoval(@Nonnull TileFluxDevice device, boolean chunkUnload) {
        if (!mToRemove.contains(device) && getLogicalEntities(ANY).contains(device)) {
            mToRemove.offer(device);
            mToAdd.remove(device);
            if (chunkUnload) {
                // create a fake device on server side, representing it has ever connected to
                // this network but currently unloaded
                mConnections.put(device.getGlobalPos(), PhantomFluxDevice.unload(device));
            } else {
                // remove the tile entity
                mConnections.remove(device.getGlobalPos());
            }
        }
    }

    /*private void addToLite(IFluxDevice flux) {
        Optional<IFluxDevice> c = all_connectors.getValue().stream().filter(f -> f.getCoords().equals(flux.getCoords
        ())).findFirst();
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
        Optional<IFluxDevice> c = all_connectors.getValue().stream().filter(f -> f.getCoords().equals(flux.getCoords
        ())).findFirst();
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
        network_players.getValue().removeIf(p -> p.getPlayerUUID().equals(uuid) && !p.getAccessPermission().canDelete
        ());
    }*/
}
