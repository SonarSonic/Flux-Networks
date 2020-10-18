package sonar.fluxnetworks.common.connection;

import it.unimi.dsi.fastutil.objects.AbstractObject2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.common.util.Constants;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Defines the base class of flux network server or
 * a class holds values updated from server for GUI display on client
 */
public class BasicFluxNetwork implements IFluxNetwork {

    //public ICustomValue<Integer> network_id = new CustomValue<>();
    //public ICustomValue<String> network_name = new CustomValue<>();
    //public ICustomValue<UUID> network_owner = new CustomValue<>();
    //public ICustomValue<SecurityType> network_security = new CustomValue<>();
    //public ICustomValue<String> network_password = new CustomValue<>();
    //public ICustomValue<Integer> network_color = new CustomValue<>();
    //public ICustomValue<EnergyType> network_energy = new CustomValue<>();
    //public ICustomValue<Integer> network_wireless = new CustomValue<>(0);
    //public ICustomValue<NetworkStatistics> network_stats = new CustomValue<>(new NetworkStatistics(this));

    private int networkID;
    private String networkName;
    private int networkColor;
    private UUID ownerUUID;

    protected final NetworkSecurity security = new NetworkSecurity();
    protected final NetworkStatistics statistics = new NetworkStatistics(this);
    protected final List<NetworkMember> memberList = new ArrayList<>();
    // On server: TileFluxDevice (loaded) and SimpleFluxDevice (unloaded)
    // On client: SimpleFluxDevice
    protected final AbstractObject2ObjectMap<GlobalPos, IFluxDevice> allConnections = new Object2ObjectOpenHashMap<>();

    public BasicFluxNetwork() {

    }

    protected BasicFluxNetwork(int id, String name, int color, UUID owner) {
        networkID = id;
        networkName = name;
        networkColor = color;
        ownerUUID = owner;
    }

    @Override
    public int getNetworkID() {
        return networkID;
    }

    @Override
    public String getNetworkName() {
        return networkName;
    }

    @Override
    public void setNetworkName(String name) {
        networkName = name;
    }

    @Override
    public int getNetworkColor() {
        return networkColor;
    }

    @Override
    public void setNetworkColor(int color) {
        networkColor = color;
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        ownerUUID = uuid;
    }

    @Override
    public NetworkSecurity getSecurity() {
        return security;
    }

    @Override
    public NetworkStatistics getStatistics() {
        return statistics;
    }

    @Override
    public List<NetworkMember> getMemberList() {
        return memberList;
    }

    @Override
    public Collection<IFluxDevice> getAllConnections() {
        return allConnections.values();
    }

    @Nullable
    @Override
    public Optional<IFluxDevice> getConnectionByPos(GlobalPos pos) {
        return Optional.ofNullable(allConnections.get(pos));
    }

    @Nonnull
    @Override
    public AccessLevel getPlayerAccess(PlayerEntity player) {
        return AccessLevel.BLOCKED;
    }

    @Nonnull
    @Override
    public <T extends IFluxDevice> List<T> getConnections(FluxLogicType type) {
        return new ArrayList<>();
    }

    @Override
    public Optional<NetworkMember> getMemberByUUID(UUID playerUUID) {
        return Optional.empty();
    }

    @Override
    public long getBufferLimiter() {
        return 0;
    }

    @Override
    public void enqueueConnectionAddition(@Nonnull IFluxDevice device) {
        device.getNetwork().enqueueConnectionRemoval(device, false);
    }

    @Override
    public void enqueueConnectionRemoval(@Nonnull IFluxDevice device, boolean chunkUnload) {

    }

    /*@Override
    public <T> T getSetting(NetworkSettings<T> setting) {
        return setting.getValue(this).getValue();
    }

    @Override
    public <T> void setSetting(NetworkSettings<T> settings, T value) {
        settings.getValue(this).setValue(value);
    }*/

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, int type) {
        if (type == FluxConstants.TYPE_NET_BASIC || type == FluxConstants.TYPE_SAVE_ALL) {
            nbt.putInt(FluxConstants.NETWORK_ID, networkID);
            nbt.putString(FluxConstants.NETWORK_NAME, networkName);
            nbt.putInt(FluxConstants.NETWORK_COLOR, networkColor);
            nbt.putUniqueId(FluxConstants.OWNER_UUID, ownerUUID);
            security.writeNBT(nbt, type == FluxConstants.TYPE_SAVE_ALL);
            //nbt.putInt(FluxNetworkData.SECURITY_TYPE, securityType.ordinal());
        }
        if (type == FluxConstants.TYPE_SAVE_ALL) {
            List<NetworkMember> members = memberList;
            if (!members.isEmpty()) {
                ListNBT list = new ListNBT();
                for (NetworkMember m : members) {
                    CompoundNBT t1 = new CompoundNBT();
                    m.writeNBT(t1);
                    list.add(t1);
                }
                nbt.put(FluxConstants.PLAYER_LIST, list);
            }

            Collection<IFluxDevice> connections = allConnections.values();
            if (!connections.isEmpty()) {
                ListNBT list = new ListNBT();
                for (IFluxDevice d : connections) {
                    if (!d.isChunkLoaded()) {
                        CompoundNBT t1 = new CompoundNBT();
                        d.writeCustomNBT(t1, FluxConstants.TYPE_SAVE_ALL);
                        list.add(t1);
                    }
                }
                nbt.put(FluxConstants.CONNECTIONS, list);
            }
        }
        if (type == FluxConstants.TYPE_NET_CONNECTIONS) {
            Collection<IFluxDevice> connections = allConnections.values();
            if (!connections.isEmpty()) {
                ListNBT list = new ListNBT();
                for (IFluxDevice d : connections) {
                    CompoundNBT t1 = new CompoundNBT();
                    d.writeCustomNBT(t1, FluxConstants.TYPE_SAVE_ALL);
                    list.add(t1);
                }
                nbt.put(FluxConstants.CONNECTIONS, list);
            }
        }
        if (type == FluxConstants.TYPE_NET_STATISTICS) {
            statistics.writeNBT(nbt);
        }
        /*if (flags == NBTType.NETWORK_GENERAL || flags == NBTType.ALL_SAVE) {
            nbt.putInt(FluxNetworkData.NETWORK_ID, network_id.getValue());
            nbt.putString(FluxNetworkData.NETWORK_NAME, network_name.getValue());
            nbt.putUniqueId(FluxNetworkData.OWNER_UUID, network_owner.getValue());
            nbt.putInt(FluxNetworkData.SECURITY_TYPE, network_security.getValue().ordinal());
            nbt.putString(FluxNetworkData.NETWORK_PASSWORD, network_password.getValue());
            nbt.putInt(FluxNetworkData.NETWORK_COLOR, network_color.getValue());
            nbt.putInt(FluxNetworkData.ENERGY_TYPE, network_energy.getValue().ordinal());
            nbt.putInt(FluxNetworkData.WIRELESS_MODE, network_wireless.getValue());

            if (flags == NBTType.ALL_SAVE) {
                FluxNetworkData.writePlayers(this, nbt);
                FluxNetworkData.writeConnections(this, nbt);
            }
        }

        if (flags == NBTType.NETWORK_PLAYERS) {
            FluxNetworkData.writeAllPlayers(this, nbt);
        }

        if (flags == NBTType.NETWORK_CONNECTIONS) {
            allDevices.getValue().removeIf(IFluxDevice::isChunkLoaded);
            List<IFluxDevice> connectors = getConnections(FluxLogicType.ANY);
            connectors.forEach(f -> allDevices.getValue().add(new SimpleFluxDevice(f)));
            FluxNetworkData.writeAllConnections(this, nbt);
        }
        if (flags == NBTType.NETWORK_STATISTICS) {
            network_stats.getValue().writeNBT(nbt);
        }
        if (flags == NBTType.NETWORK_CLEAR) {
            nbt.putBoolean("clear", true); // Nothing
        }*/
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, int type) {
        if (type == FluxConstants.TYPE_NET_BASIC || type == FluxConstants.TYPE_SAVE_ALL) {
            networkID = nbt.getInt(FluxConstants.NETWORK_ID);
            networkName = nbt.getString(FluxConstants.NETWORK_NAME);
            networkColor = nbt.getInt(FluxConstants.NETWORK_COLOR);
            ownerUUID = nbt.getUniqueId(FluxConstants.OWNER_UUID);
            security.readNBT(nbt);
        }
        if (type == FluxConstants.TYPE_SAVE_ALL) {
            ListNBT list = nbt.getList(FluxConstants.PLAYER_LIST, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT c = list.getCompound(i);
                memberList.add(new NetworkMember(c));
            }
            list = nbt.getList(FluxConstants.CONNECTIONS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT c = list.getCompound(i);
                GlobalPos pos = FluxUtils.readGlobalPos(c);
                allConnections.put(pos, new PhantomFluxDevice(pos, c));
            }
        }
        if (type == FluxConstants.TYPE_NET_CONNECTIONS) {
            ListNBT list = nbt.getList(FluxConstants.CONNECTIONS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT c = list.getCompound(i);
                GlobalPos pos = FluxUtils.readGlobalPos(c);
                allConnections.put(pos, new PhantomFluxDevice(pos, c));
            }
        }
        if (type == FluxConstants.TYPE_NET_STATISTICS) {
            statistics.readNBT(nbt);
        }
        /*if (flags == NBTType.NETWORK_GENERAL || flags == NBTType.ALL_SAVE) {
            network_id.setValue(nbt.getInt(FluxNetworkData.NETWORK_ID));
            network_name.setValue(nbt.getString(FluxNetworkData.NETWORK_NAME));
            network_owner.setValue(nbt.getUniqueId(FluxNetworkData.OWNER_UUID));
            network_security.setValue(SecurityType.values()[nbt.getInt(FluxNetworkData.SECURITY_TYPE)]);
            network_password.setValue(nbt.getString(FluxNetworkData.NETWORK_PASSWORD));
            network_color.setValue(nbt.getInt(FluxNetworkData.NETWORK_COLOR));
            network_energy.setValue(EnergyType.values()[nbt.getInt(FluxNetworkData.ENERGY_TYPE)]);
            network_wireless.setValue(nbt.getInt(FluxNetworkData.WIRELESS_MODE));

            if (flags == NBTType.ALL_SAVE) {
                FluxNetworkData.readPlayers(this, nbt);
                FluxNetworkData.readConnections(this, nbt);
            }
        }

        if (flags == NBTType.NETWORK_PLAYERS) {
            FluxNetworkData.readPlayers(this, nbt);
        }

        if (flags == NBTType.NETWORK_CONNECTIONS) {
            FluxNetworkData.readAllConnections(this, nbt);
        }
        if (flags == NBTType.NETWORK_STATISTICS) {
            network_stats.getValue().readNBT(nbt);
        }*/
    }
}
