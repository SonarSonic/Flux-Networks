package fluxnetworks.common.network;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.*;
import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.network.ISuperAdmin;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.data.FluxNetworkData;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.handler.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
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
                if(network.getSetting(NetworkSettings.NETWORK_COLOR) != color) {
                    network.setSetting(NetworkSettings.NETWORK_COLOR, color);
                    @SuppressWarnings("unchecked")
                    List<IFluxConnector> list = network.getConnections(FluxType.flux);
                    list.forEach(fluxConnector -> fluxConnector.connect(network)); // update color data
                    FluxNetworks.proxy.clearColorCache(networkID);
                }
                network.setSetting(NetworkSettings.NETWORK_SECURITY, security);
                network.setSetting(NetworkSettings.NETWORK_ENERGY, energy);
                network.setSetting(NetworkSettings.NETWORK_PASSWORD, password);
                PacketHandler.network.sendToAll(new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), NBTType.NETWORK_GENERAL));
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS_2);
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
                return new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
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
                return new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }

    public static NBTTagCompound getChangePermissionPacket(int networkID, UUID playerChanged, int type) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setUniqueId("playerChanged", playerChanged);
        tag.setInteger("t", type);
        return tag;
    }

    public static IMessage handleChangePermissionPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        UUID playerChanged = packetTag.getUniqueId("playerChanged");
        int type = packetTag.getInteger("t");
        if (playerChanged != null) {
            /*if (EntityPlayer.getUUID(player.getGameProfile()).equals(playerChanged)) {
                //don't allow editing of their own permissions...
                return null;
            }*/

            IFluxNetwork network = FluxNetworkCache.instance.getNetwork(networkID);
            if (!network.isInvalid()) {
                if (network.getMemberPermission(player).canEdit()) {
                    // Create new member
                    if(type == 0) {
                        EntityPlayer player1 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerChanged);
                        //noinspection ConstantConditions
                        if(player1 != null) {
                            NetworkMember newMember = NetworkMember.createNetworkMember(player1, AccessPermission.USER);
                            network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(newMember);
                            return new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
                        }
                        return new PacketFeedback.FeedbackMessage(FeedbackInfo.INVALID_USER);
                    } else {
                        Optional<NetworkMember> settings = network.getValidMember(playerChanged);
                        if (settings.isPresent()) {
                            NetworkMember p = settings.get();
                            if (type == 1) {
                                p.setAccessPermission(AccessPermission.ADMIN);
                            } else if(type == 2) {
                                p.setAccessPermission(AccessPermission.USER);
                            } else if(type == 3) {
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).remove(p);
                            } else if(type == 4) {
                                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*/
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).removeIf(f -> f.getAccessPermission().canDelete());
                                p.setAccessPermission(AccessPermission.OWNER);
                            }
                            return new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
                        } else if(type == 4) {
                            EntityPlayer player1 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerChanged);
                            //noinspection ConstantConditions
                            if(player1 != null) {
                                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*/
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).removeIf(f -> f.getAccessPermission().canDelete());
                                NetworkMember newMember = NetworkMember.createNetworkMember(player1, AccessPermission.OWNER);
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(newMember);
                                return new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
                            }
                            return new PacketFeedback.FeedbackMessage(FeedbackInfo.INVALID_USER);
                        }
                        return new PacketFeedback.FeedbackMessage(FeedbackInfo.INVALID_USER);
                    }
                } else {
                    return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
                }
            }
        }
        return null;
    }

    public static NBTTagCompound getChangeWirelessPacket(int networkID, int wirelessMode) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setInteger(FluxNetworkData.WIRELESS_MODE, wirelessMode);
        return tag;
    }

    public static IMessage handleChangeWirelessPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        int wireless = packetTag.getInteger(FluxNetworkData.WIRELESS_MODE);
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(networkID);
        if (!network.isInvalid()) {
            if(network.getMemberPermission(player).canEdit()) {
                network.setSetting(NetworkSettings.NETWORK_WIRELESS, wireless);
                PacketHandler.network.sendTo(new PacketNetworkUpdate.NetworkUpdateMessage(Lists.newArrayList(network), NBTType.NETWORK_GENERAL), (EntityPlayerMP) player);
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS);
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }
}
