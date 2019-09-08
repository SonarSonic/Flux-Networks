package fluxnetworks.common.network;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.client.FluxColorHandler;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.data.FluxNetworkData;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PacketGeneralHandler {

    public static NBTTagCompound getCreateNetworkPacket(String name, int color, SecurityType security, EnergyType energy, String password) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(FluxNetworkData.NETWORK_NAME, name);
        tag.setInteger(FluxNetworkData.NETWORK_COLOR, color);
        tag.setInteger(FluxNetworkData.SECURITY_TYPE, security.ordinal());
        tag.setInteger(FluxNetworkData.ENERGY_TYPE, energy.ordinal());
        tag.setString(FluxNetworkData.NETWORK_PASSWORD, password);
        return tag;
    }

    public static IMessage handleCreateNetworkPacket(EntityPlayer player, NBTTagCompound nbtTag) {
        String name = nbtTag.getString(FluxNetworkData.NETWORK_NAME);
        int color = nbtTag.getInteger(FluxNetworkData.NETWORK_COLOR);
        SecurityType security = SecurityType.values()[nbtTag.getInteger(FluxNetworkData.SECURITY_TYPE)];
        EnergyType energy = EnergyType.values()[nbtTag.getInteger(FluxNetworkData.ENERGY_TYPE)];
        String password = nbtTag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if(!FluxUtils.checkPassword(password)) {
            return new PacketFeedback.FeedbackMessage(FeedbackInfo.ILLEGAL_PASSWORD);
        }
        if(FluxNetworkCache.instance.hasSpaceLeft(player)) {
            FluxNetworkCache.instance.createdNetwork(player, name, color, security, energy, password);
            return new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS);
        }
        return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_SPACE);
    }

    public static NBTTagCompound getNetworkEditPacket(int networkID, String networkName, int color, SecurityType security, EnergyType energy, String password) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setString(FluxNetworkData.NETWORK_NAME, networkName);
        tag.setInteger(FluxNetworkData.NETWORK_COLOR, color);
        tag.setInteger(FluxNetworkData.SECURITY_TYPE, security.ordinal());
        tag.setInteger(FluxNetworkData.ENERGY_TYPE, energy.ordinal());
        tag.setString(FluxNetworkData.NETWORK_PASSWORD, password);
        return tag;
    }

    public static IMessage handleNetworkEditPacket(EntityPlayer player, NBTTagCompound tag) {
        int networkID = tag.getInteger(FluxNetworkData.NETWORK_ID);
        String newName = tag.getString(FluxNetworkData.NETWORK_NAME);
        int color = tag.getInteger(FluxNetworkData.NETWORK_COLOR);
        SecurityType security = SecurityType.values()[tag.getInteger(FluxNetworkData.SECURITY_TYPE)];
        EnergyType energy = EnergyType.values()[tag.getInteger(FluxNetworkData.ENERGY_TYPE)];
        String password = tag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if(!FluxUtils.checkPassword(password)) {
            return new PacketFeedback.FeedbackMessage(FeedbackInfo.ILLEGAL_PASSWORD);
        }
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(networkID);
        if (!network.isInvalid()) {
            if (network.getMemberPermission(player).canEdit()) {
                network.setSetting(NetworkSettings.NETWORK_NAME, newName);
                network.setSetting(NetworkSettings.NETWORK_COLOR, color);
                network.setSetting(NetworkSettings.NETWORK_SECURITY, security);
                network.setSetting(NetworkSettings.NETWORK_ENERGY, energy);
                network.setSetting(NetworkSettings.NETWORK_PASSWORD, password);
                List<IFluxConnector> list = network.getConnections(FluxType.flux);
                list.forEach(fluxConnector -> fluxConnector.connect(network)); // reconnect
                FluxNetworks.proxy.clearColorCache(networkID);
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS);
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }

    public static NBTTagCompound getDeleteNetworkPacket(int networkID) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        return tag;
    }

    public static IMessage handleDeleteNetworkPacket(EntityPlayer player, NBTTagCompound nbtTag) {
        int id = nbtTag.getInteger(FluxNetworkData.NETWORK_ID);
        IFluxNetwork toDelete = FluxNetworkCache.instance.getNetwork(id);
        if(!toDelete.isInvalid()) {
            if(toDelete.getMemberPermission(player).canDelete()) {
                FluxNetworkData.get().removeNetwork(toDelete);
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS);
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_OWNER);
            }
        }
        return null;
    }

    public static NBTTagCompound getAddMemberPacket(int networkID, String playerName) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setString("playerName", playerName);
        return tag;
    }

    public static IMessage handleAddMemberPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        String playerName = packetTag.getString("playerName");

        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(networkID);
        if (!network.isInvalid()) {
            if (network.getMemberPermission(player).canEdit()) {
                network.addNewMember(playerName);
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }

    public static NBTTagCompound getRemoveMemberPacket(int networkID, UUID playerRemoved) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setUniqueId("playerRemoved", playerRemoved);
        return tag;
    }

    public static IMessage handleRemoveMemberPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        UUID playerRemoved = packetTag.getUniqueId("playerRemoved");

        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(networkID);
        if (!network.isInvalid()) {
            if (network.getMemberPermission(player).canEdit()) {
                network.removeMember(playerRemoved);
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }

    public static NBTTagCompound getChangePermissionPacket(int networkID, UUID playerChanged) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setUniqueId("playerChanged", playerChanged);
        return tag;
    }

    public static IMessage handleChangePermissionPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        UUID playerChanged = packetTag.getUniqueId("playerChanged");
        if (playerChanged != null) {
            if (EntityPlayer.getUUID(player.getGameProfile()).equals(playerChanged)) {
                //don't allow editing of their own permissions...
                return null;
            }

            IFluxNetwork network = FluxNetworkCache.instance.getNetwork(networkID);
            if (!network.isInvalid()) {
                if (network.getMemberPermission(player).canEdit()) {
                    Optional<NetworkMember> settings = network.getValidMember(playerChanged);
                    if (settings.isPresent()) {
                        NetworkMember p = settings.get();
                        if(!p.getPermission().canDelete()) {
                            p.setPermission(p.getPermission() == AccessPermission.USER ? AccessPermission.ADMIN : AccessPermission.USER);
                            return null;
                        }
                    }
                    return new PacketFeedback.FeedbackMessage(FeedbackInfo.INVALID_USER);

                } else {
                    return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
                }
            }
        }
        return null;
    }

    public static NBTTagCompound getChangeWirelessPacket(int networkID, boolean change) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setBoolean(FluxNetworkData.WIRELESS_MODE, change);
        return tag;
    }

    public static IMessage handleChangeWirelessPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        boolean wireless = packetTag.getBoolean(FluxNetworkData.WIRELESS_MODE);
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(networkID);
        if (!network.isInvalid()) {
            if(network.getMemberPermission(player).canEdit()) {
                network.setSetting(NetworkSettings.NETWORK_WIRELESS, wireless);
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }
}
