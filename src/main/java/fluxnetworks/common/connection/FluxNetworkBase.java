package fluxnetworks.common.connection;

import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.core.CustomValue;
import fluxnetworks.common.core.ICustomValue;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.data.FluxNetworkData;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public abstract class FluxNetworkBase implements IFluxNetwork {

    public ICustomValue<Integer> network_id = new CustomValue<>();
    public ICustomValue<String> network_name = new CustomValue<>();
    public ICustomValue<UUID> network_owner = new CustomValue<>();
    public ICustomValue<SecurityType> network_security = new CustomValue<>();
    public ICustomValue<String> network_password = new CustomValue<>();
    public ICustomValue<Integer> network_color = new CustomValue<>();
    public ICustomValue<EnergyType> network_energy = new CustomValue<>();
    public ICustomValue<Integer> network_wireless = new CustomValue<>(0);

    public ICustomValue<NetworkStatistics> network_stats = new CustomValue<>(new NetworkStatistics(this));
    public ICustomValue<List<IFluxConnector>> all_connectors = new CustomValue<>(new ArrayList<>());
    public ICustomValue<List<NetworkMember>> network_players = new CustomValue<>(new ArrayList<>());

    public FluxNetworkBase() {}

    public FluxNetworkBase(int id, String name, SecurityType security, int color, UUID owner, EnergyType energy, String password) {
        network_id.setValue(id);
        network_name.setValue(name);
        network_security.setValue(security);
        network_color.setValue(color);
        network_owner.setValue(owner);
        network_energy.setValue(energy);
        network_password.setValue(password);
    }

    @Override
    public <T> T getSetting(NetworkSettings<T> setting){
        return (T) setting.getValue(this).getValue();
    }

    @Override
    public <T> void setSetting(NetworkSettings<T> settings, T value) {
        settings.getValue(this).setValue(value);
    }

    @Override
    public void readNetworkNBT(NBTTagCompound nbt, NBTType type) {
        if(type == NBTType.NETWORK_GENERAL || type == NBTType.ALL_SAVE) {
            network_id.setValue(nbt.getInteger(FluxNetworkData.NETWORK_ID));
            network_name.setValue(nbt.getString(FluxNetworkData.NETWORK_NAME));
            network_owner.setValue(nbt.getUniqueId(FluxNetworkData.OWNER_UUID));
            network_security.setValue(SecurityType.values()[nbt.getInteger(FluxNetworkData.SECURITY_TYPE)]);
            network_password.setValue(nbt.getString(FluxNetworkData.NETWORK_PASSWORD));
            network_color.setValue(nbt.getInteger(FluxNetworkData.NETWORK_COLOR));
            network_energy.setValue(EnergyType.values()[nbt.getInteger(FluxNetworkData.ENERGY_TYPE)]);
            network_wireless.setValue(nbt.getInteger(FluxNetworkData.WIRELESS_MODE));

            if(type == NBTType.ALL_SAVE) {
                FluxNetworkData.readPlayers(this, nbt);
                FluxNetworkData.readConnections(this, nbt);
            }
        }

        if(type == NBTType.NETWORK_PLAYERS) {
            FluxNetworkData.readPlayers(this, nbt);
        }

        if(type == NBTType.NETWORK_CONNECTIONS) {
            FluxNetworkData.readAllConnections(this, nbt);
        }
        if(type == NBTType.NETWORK_STATISTICS) {
            network_stats.getValue().readNBT(nbt);
        }
    }

    @Override
    public NBTTagCompound writeNetworkNBT(NBTTagCompound nbt, NBTType type) {
        if(type == NBTType.NETWORK_GENERAL || type == NBTType.ALL_SAVE) {
            nbt.setInteger(FluxNetworkData.NETWORK_ID, network_id.getValue());
            nbt.setString(FluxNetworkData.NETWORK_NAME, network_name.getValue());
            nbt.setUniqueId(FluxNetworkData.OWNER_UUID, network_owner.getValue());
            nbt.setInteger(FluxNetworkData.SECURITY_TYPE, network_security.getValue().ordinal());
            nbt.setString(FluxNetworkData.NETWORK_PASSWORD, network_password.getValue());
            nbt.setInteger(FluxNetworkData.NETWORK_COLOR, network_color.getValue());
            nbt.setInteger(FluxNetworkData.ENERGY_TYPE, network_energy.getValue().ordinal());
            nbt.setInteger(FluxNetworkData.WIRELESS_MODE, network_wireless.getValue());

            if(type == NBTType.ALL_SAVE) {
                FluxNetworkData.writePlayers(this, nbt);
                FluxNetworkData.writeConnections(this, nbt);
            }
        }

        if(type == NBTType.NETWORK_PLAYERS) {
            FluxNetworkData.writePlayers(this, nbt);
        }

        if(type == NBTType.NETWORK_CONNECTIONS) {
            all_connectors.getValue().removeIf(IFluxConnector::isChunkLoaded);
            @SuppressWarnings("unchecked")
            List<IFluxConnector> connectors = getConnections(FluxType.flux);
            connectors.forEach(f -> all_connectors.getValue().add(new FluxLiteConnector(f)));
            FluxNetworkData.writeAllConnections(this, nbt);
        }
        if(type == NBTType.NETWORK_STATISTICS) {
            network_stats.getValue().writeNBT(nbt);
        }
        if(type == NBTType.NETWORK_CLEAR) {
            nbt.setBoolean("clear", true); // Nothing
        }

        return nbt;
    }

}
