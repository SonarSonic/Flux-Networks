package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.*;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines the base class of flux network server or
 * a class holds values updated from server for GUI display on client
 */
public class SimpleFluxNetwork implements IFluxNetwork {

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
    private UUID ownerUUID;
    protected SecurityType securityType;
    @Nullable
    private String networkPassword;
    private int networkColor;

    protected final NetworkStatistics statistics = new NetworkStatistics(this);
    // On server: TileFluxDevice (loaded) and SimpleFluxDevice (unloaded)
    // On client: SimpleFluxDevice
    protected final List<IFluxDevice> allDevices = new ArrayList<>();
    protected final List<NetworkMember> networkMembers = new ArrayList<>();

    public SimpleFluxNetwork() {

    }

    public SimpleFluxNetwork(int id, String name, SecurityType security, int color, UUID owner, @Nullable String password) {
        networkID = id;
        networkName = name;
        securityType = security;
        networkColor = color;
        ownerUUID = owner;
        networkPassword = password;
    }

    @Nonnull
    @Override
    public AccessType getPlayerAccess(PlayerEntity player) {
        return AccessType.BLOCKED;
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
    public void enqueueConnectionAddition(@Nonnull IFluxDevice device) {
        device.getNetwork().enqueueConnectionRemoval(device, false);
    }

    @Override
    public void enqueueConnectionRemoval(@Nonnull IFluxDevice device, boolean chunkUnload) {

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

    /*@Override
    public <T> T getSetting(NetworkSettings<T> setting) {
        return setting.getValue(this).getValue();
    }

    @Override
    public <T> void setSetting(NetworkSettings<T> settings, T value) {
        settings.getValue(this).setValue(value);
    }*/

    @Override
    public SecurityType getNetworkSecurity() {
        return securityType;
    }

    @Nullable
    @Override
    public String getNetworkPassword() {
        return networkPassword;
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
    public NetworkStatistics getNetworkStatistics() {
        return statistics;
    }

    @Override
    public List<NetworkMember> getMemberList() {
        return networkMembers;
    }

    @Override
    public List<IFluxDevice> getAllDevices() {
        return allDevices;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, int flags) {
        if ((flags & FluxConstants.FLAG_NET_BASIS) != 0 || (flags & FluxConstants.FLAG_SAVE_ALL) != 0) {
            networkID = nbt.getInt(FluxNetworkData.NETWORK_ID);
            networkName = nbt.getString(FluxNetworkData.NETWORK_NAME);
            ownerUUID = nbt.getUniqueId(FluxNetworkData.OWNER_UUID);
            securityType = SecurityType.values()[nbt.getInt(FluxNetworkData.SECURITY_TYPE)];
            networkColor = nbt.getInt(FluxNetworkData.NETWORK_COLOR);
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

    @Override
    public void writeCustomNBT(CompoundNBT nbt, int flags) {
        if ((flags & FluxConstants.FLAG_NET_BASIS) != 0 || (flags & FluxConstants.FLAG_SAVE_ALL) != 0) {
            nbt.putInt(FluxNetworkData.NETWORK_ID, networkID);
            nbt.putString(FluxNetworkData.NETWORK_NAME, networkName);
            nbt.putUniqueId(FluxNetworkData.OWNER_UUID, ownerUUID);
            nbt.putInt(FluxNetworkData.SECURITY_TYPE, securityType.ordinal());
            nbt.putInt(FluxNetworkData.NETWORK_COLOR, networkColor);
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
}
