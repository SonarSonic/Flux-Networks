package sonar.fluxnetworks.common.connection;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.device.*;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class controls a flux Network on server thread.
 */
public class ServerFluxNetwork extends FluxNetwork {

    private static final Comparator<TileFluxDevice> sDescendingOrder =
            (a, b) -> Integer.compare(b.getTransferNode().getPriority(), a.getTransferNode().getPriority());

    private static final Consumer<TileFluxDevice> sDisconnect = d -> d.connect(FluxNetwork.WILDCARD);

    private static final Class<?>[] sLogicalTypes =
            {IFluxDevice.class, IFluxPlug.class, IFluxPoint.class, IFluxStorage.class, IFluxController.class};

    private final ArrayList<TileFluxDevice>[] mDevices;

    // LinkedList doesn't create large arrays, no need to use ArrayDeque
    private final LinkedList<TileFluxDevice> mToAdd = new LinkedList<>();
    private final LinkedList<TileFluxDevice> mToRemove = new LinkedList<>();

    private boolean mSortConnections = true;

    private final TransferIterator mPlugTransferIterator = new TransferIterator(false);
    private final TransferIterator mPointTransferIterator = new TransferIterator(true);

    private long mBufferLimiter = 0;

    private String mPassword;

    {
        @SuppressWarnings("unchecked") final ArrayList<TileFluxDevice>[] devices =
                (ArrayList<TileFluxDevice>[]) Array.newInstance(ArrayList.class, sLogicalTypes.length);
        Arrays.setAll(devices, i -> new ArrayList<>());
        mDevices = devices;
    }

    ServerFluxNetwork() {
    }

    ServerFluxNetwork(int id, String name, int color, @Nonnull SecurityLevel security, @Nonnull Player owner,
                      @Nonnull String password) {
        super(id, name, color, security, owner);
        mPassword = password;
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
            for (int i = 0; i < sLogicalTypes.length; i++) {
                if (sLogicalTypes[i].isInstance(device)) {
                    ArrayList<TileFluxDevice> list = getLogicalEntities(i);
                    assert !list.contains(device);
                    mSortConnections |= list.add(device);
                }
            }
        }
        while ((device = mToRemove.poll()) != null) {
            for (int i = 0; i < sLogicalTypes.length; i++) {
                if (sLogicalTypes[i].isInstance(device)) {
                    ArrayList<TileFluxDevice> list = getLogicalEntities(i);
                    assert list.contains(device);
                    mSortConnections |= list.remove(device);
                }
            }
        }
        if (mSortConnections) {
            getLogicalEntities(PLUG).sort(sDescendingOrder);
            getLogicalEntities(POINT).sort(sDescendingOrder);
            mSortConnections = false;
        }
    }

    @Nonnull
    @Override
    public ArrayList<TileFluxDevice> getLogicalEntities(int logic) {
        return mDevices[logic];
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
            // push into stack because they called too many times below
            final TransferIterator plugIterator = mPlugTransferIterator.reset(plugs);
            final TransferIterator pointIterator = mPointTransferIterator.reset(points);
            CYCLE:
            while (pointIterator.hasNext()) {
                while (plugIterator.hasNext()) {
                    TileFluxDevice plug = plugIterator.next();
                    TileFluxDevice point = pointIterator.next();
                    if (plug.getDeviceType().is(point)) {
                        break CYCLE; // Storage always have the lowest priority, the cycle can be broken here.
                    }
                    // we don't need to simulate this action
                    long actual = plug.getTransferNode().extract(point.getTransferNode().getRequest());
                    if (actual > 0) {
                        point.getTransferNode().insert(actual);
                        continue CYCLE;
                    } else {
                        // although the plug still need transfer (buffer > 0)
                        // but it reached max transfer limit, so we use next plug
                        plugIterator.increment();
                    }
                }
                break; // all plugs have been used
            }
        }

        long limiter = 0;
        for (var f : devices) {
            f.getTransferNode().onCycleEnd();
            limiter += f.getTransferNode().getRequest();
        }
        mBufferLimiter = limiter;

        mStatistics.stopProfiling();
    }

    @Override
    public long getBufferLimiter() {
        return mBufferLimiter;
    }

    @Nonnull
    @Override
    public AccessLevel getPlayerAccess(@Nonnull Player player) {
        if (FluxPlayer.isPlayerSuperAdmin(player)) {
            return AccessLevel.SUPER_ADMIN;
        }
        return super.getPlayerAccess(player);
    }

    @Override
    public void onDelete() {
        super.onDelete();
        getLogicalEntities(ANY).forEach(sDisconnect);
        Arrays.fill(mDevices, null);
        mToAdd.clear();
        mToRemove.clear();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean enqueueConnectionAddition(@Nonnull TileFluxDevice device) {
        if (device.getDeviceType().isController() && getLogicalEntities(CONTROLLER).size() > 0) {
            return false;
        }
        if (!mToAdd.contains(device) && !getLogicalEntities(ANY).contains(device)) {
            mToAdd.offer(device);
            mToRemove.remove(device);
            mConnectionMap.put(device.getGlobalPos(), device);
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
                mConnectionMap.put(device.getGlobalPos(), PhantomFluxDevice.unload(device));
            } else {
                // remove the tile entity
                mConnectionMap.remove(device.getGlobalPos());
            }
        }
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, int type) {
        super.writeCustomTag(tag, type);
        if (type == FluxConstants.TYPE_SAVE_ALL) {
            tag.putString("password", mPassword);
        }
    }

    @Override
    public void readCustomTag(@Nonnull CompoundTag tag, int type) {
        super.readCustomTag(tag, type);
        mPassword = tag.getString("password");
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
