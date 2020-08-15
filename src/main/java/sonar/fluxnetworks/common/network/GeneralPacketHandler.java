package sonar.fluxnetworks.common.network;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.FluxCacheType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.tiles.IFluxDevice;
import sonar.fluxnetworks.api.network.EnumAccessType;
import sonar.fluxnetworks.api.network.EnumSecurityType;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.api.network.NetworkMember;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.handler.PacketHandler;
import net.minecraft.util.Tuple;

import java.util.*;

public class GeneralPacketHandler {

    public static CompoundNBT getCreateNetworkPacket(String name, int color, EnumSecurityType security, EnergyType energy, String password) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString(FluxNetworkData.NETWORK_NAME, name);
        tag.putInt(FluxNetworkData.NETWORK_COLOR, color);
        tag.putInt(FluxNetworkData.SECURITY_TYPE, security.ordinal());
        tag.putInt(FluxNetworkData.ENERGY_TYPE, energy.ordinal());
        tag.putString(FluxNetworkData.NETWORK_PASSWORD, password);
        return tag;
    }

    public static Object handleCreateNetworkPacket(PlayerEntity player, CompoundNBT nbtTag) {
        String name = nbtTag.getString(FluxNetworkData.NETWORK_NAME);
        int color = nbtTag.getInt(FluxNetworkData.NETWORK_COLOR);
        EnumSecurityType security = EnumSecurityType.values()[nbtTag.getInt(FluxNetworkData.SECURITY_TYPE)];
        EnergyType energy = EnergyType.values()[nbtTag.getInt(FluxNetworkData.ENERGY_TYPE)];
        String password = nbtTag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if(!FluxUtils.checkPassword(password)) {
            return new FeedbackPacket(EnumFeedbackInfo.ILLEGAL_PASSWORD);
        }
        if(FluxNetworkCache.INSTANCE.hasSpaceLeft(player)) {
            FluxNetworkCache.INSTANCE.createdNetwork(player, name, color, security, energy, password);
            return new FeedbackPacket(EnumFeedbackInfo.SUCCESS);
        }
        return new FeedbackPacket(EnumFeedbackInfo.NO_SPACE);
    }

    public static CompoundNBT getNetworkEditPacket(int networkID, String networkName, int color, EnumSecurityType security, EnergyType energy, String password) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxNetworkData.NETWORK_ID, networkID);
        tag.putString(FluxNetworkData.NETWORK_NAME, networkName);
        tag.putInt(FluxNetworkData.NETWORK_COLOR, color);
        tag.putInt(FluxNetworkData.SECURITY_TYPE, security.ordinal());
        tag.putInt(FluxNetworkData.ENERGY_TYPE, energy.ordinal());
        tag.putString(FluxNetworkData.NETWORK_PASSWORD, password);
        return tag;
    }

    public static Object handleNetworkEditPacket(PlayerEntity player, CompoundNBT tag) {
        int networkID = tag.getInt(FluxNetworkData.NETWORK_ID);
        String newName = tag.getString(FluxNetworkData.NETWORK_NAME);
        int color = tag.getInt(FluxNetworkData.NETWORK_COLOR);
        EnumSecurityType security = EnumSecurityType.values()[tag.getInt(FluxNetworkData.SECURITY_TYPE)];
        EnergyType energy = EnergyType.values()[tag.getInt(FluxNetworkData.ENERGY_TYPE)];
        String password = tag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if(!FluxUtils.checkPassword(password)) {
            return new FeedbackPacket(EnumFeedbackInfo.ILLEGAL_PASSWORD);
        }
        IFluxNetwork network = FluxNetworkCache.INSTANCE.getNetwork(networkID);
        if (network.isValid()) {
            if (network.getMemberPermission(player).canEdit()) {
                boolean needPacket = false;
                if(!network.getSetting(NetworkSettings.NETWORK_NAME).equals(newName)) {
                    network.setSetting(NetworkSettings.NETWORK_NAME, newName);
                    needPacket = true;
                }
                if(network.getSetting(NetworkSettings.NETWORK_COLOR) != color) {
                    network.setSetting(NetworkSettings.NETWORK_COLOR, color);
                    needPacket = true;
                    List<IFluxDevice> list = network.getConnections(FluxCacheType.FLUX);
                    list.forEach(fluxConnector -> fluxConnector.connect(network)); // update color data
                }
                if(needPacket) {
                    HashMap<Integer, Tuple<Integer, String>> cache = new HashMap<>();
                    cache.put(networkID, new Tuple<>(network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000, network.getSetting(NetworkSettings.NETWORK_NAME)));
                    PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new NetworkColourPacket(cache));
                }
                network.setSetting(NetworkSettings.NETWORK_SECURITY, security);
                network.setSetting(NetworkSettings.NETWORK_ENERGY, energy);
                network.setSetting(NetworkSettings.NETWORK_PASSWORD, password);
                PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_GENERAL));
                return new FeedbackPacket(EnumFeedbackInfo.SUCCESS_2);
            } else {
                return new FeedbackPacket(EnumFeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }

    public static CompoundNBT getDeleteNetworkPacket(int networkID) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxNetworkData.NETWORK_ID, networkID);
        return tag;
    }

    public static Object handleDeleteNetworkPacket(PlayerEntity player, CompoundNBT nbtTag) {
        int id = nbtTag.getInt(FluxNetworkData.NETWORK_ID);
        IFluxNetwork toDelete = FluxNetworkCache.INSTANCE.getNetwork(id);
        if(toDelete.isValid()) {
            if(toDelete.getMemberPermission(player).canDelete()) {
                FluxNetworkData.get().deleteNetwork(toDelete);
                return new FeedbackPacket(EnumFeedbackInfo.SUCCESS);
            } else {
                return new FeedbackPacket(EnumFeedbackInfo.NO_OWNER);
            }
        }
        return null;
    }

    @Deprecated
    public static CompoundNBT getAddMemberPacket(int networkID, String playerName) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxNetworkData.NETWORK_ID, networkID);
        tag.putString("playerName", playerName);
        return tag;
    }

    @Deprecated
    public static Object handleAddMemberPacket(PlayerEntity player, CompoundNBT packetTag) {
        int networkID = packetTag.getInt(FluxNetworkData.NETWORK_ID);
        String playerName = packetTag.getString("playerName");

        IFluxNetwork network = FluxNetworkCache.INSTANCE.getNetwork(networkID);
        if (network.isValid()) {
            if (network.getMemberPermission(player).canEdit()) {
                network.addNewMember(playerName);
                return new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
            } else {
                return new FeedbackPacket(EnumFeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }

    @Deprecated
    public static CompoundNBT getRemoveMemberPacket(int networkID, UUID playerRemoved) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxNetworkData.NETWORK_ID, networkID);
        tag.putUniqueId("playerRemoved", playerRemoved);
        return tag;
    }

    @Deprecated
    public static Object handleRemoveMemberPacket(PlayerEntity player, CompoundNBT packetTag) {
        int networkID = packetTag.getInt(FluxNetworkData.NETWORK_ID);
        UUID playerRemoved = packetTag.getUniqueId("playerRemoved");

        IFluxNetwork network = FluxNetworkCache.INSTANCE.getNetwork(networkID);
        if (network.isValid()) {
            if (network.getMemberPermission(player).canEdit()) {
                network.removeMember(playerRemoved);
                return new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
            } else {
                return new FeedbackPacket(EnumFeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }

    public static CompoundNBT getChangePermissionPacket(int networkID, UUID playerChanged, int type) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxNetworkData.NETWORK_ID, networkID);
        tag.putUniqueId("playerChanged", playerChanged);
        tag.putInt("t", type);
        return tag;
    }

    public static Object handleChangePermissionPacket(PlayerEntity player, CompoundNBT packetTag) {
        int networkID = packetTag.getInt(FluxNetworkData.NETWORK_ID);
        UUID playerChanged = packetTag.getUniqueId("playerChanged");
        int type = packetTag.getInt("t");
        if (playerChanged != null) {
            /*if (PlayerEntity.getUUID(player.getGameProfile()).equals(playerChanged)) {
                //don't allow editing of their own permissions...
                return null;
            }*/

            IFluxNetwork network = FluxNetworkCache.INSTANCE.getNetwork(networkID);
            if (network.isValid()) {
                if (network.getMemberPermission(player).canEdit()) {
                    // Create new member
                    if(type == 0) {
                        PlayerEntity player1 = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerChanged);
                        //noinspection
                        if(player1 != null) {
                            NetworkMember newMember = NetworkMember.createNetworkMember(player1, EnumAccessType.USER);
                            network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(newMember);
                            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new FeedbackPacket(EnumFeedbackInfo.SUCCESS));
                            return new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
                        }
                        return new FeedbackPacket(EnumFeedbackInfo.INVALID_USER);
                    } else {
                        Optional<NetworkMember> settings = network.getValidMember(playerChanged);
                        if (settings.isPresent()) {
                            NetworkMember p = settings.get();
                            if (type == 1) {
                                p.setAccessPermission(EnumAccessType.ADMIN);
                            } else if(type == 2) {
                                p.setAccessPermission(EnumAccessType.USER);
                            } else if(type == 3) {
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).remove(p);
                            } else if(type == 4) {
                                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*/
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).removeIf(f -> f.getAccessPermission().canDelete());
                                network.setSetting(NetworkSettings.NETWORK_OWNER, playerChanged);
                                p.setAccessPermission(EnumAccessType.OWNER);
                            }
                            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new FeedbackPacket(EnumFeedbackInfo.SUCCESS));
                            return new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
                        } else if(type == 4) {
                            PlayerEntity player1 = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerChanged);
                            //noinspection
                            if(player1 != null) {
                                /*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*/
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).removeIf(f -> f.getAccessPermission().canDelete());
                                NetworkMember newMember = NetworkMember.createNetworkMember(player1, EnumAccessType.OWNER);
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(newMember);
                                network.setSetting(NetworkSettings.NETWORK_OWNER, playerChanged);
                                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new FeedbackPacket(EnumFeedbackInfo.SUCCESS));
                                return new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
                            }
                            return new FeedbackPacket(EnumFeedbackInfo.INVALID_USER);
                        }
                        return new FeedbackPacket(EnumFeedbackInfo.INVALID_USER);
                    }
                } else {
                    return new FeedbackPacket(EnumFeedbackInfo.NO_ADMIN);
                }
            }
        }
        return null;
    }

    public static CompoundNBT getChangeWirelessPacket(int networkID, int wirelessMode) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxNetworkData.NETWORK_ID, networkID);
        tag.putInt(FluxNetworkData.WIRELESS_MODE, wirelessMode);
        return tag;
    }

    public static Object handleChangeWirelessPacket(PlayerEntity player, CompoundNBT packetTag) {
        int networkID = packetTag.getInt(FluxNetworkData.NETWORK_ID);
        int wireless = packetTag.getInt(FluxNetworkData.WIRELESS_MODE);
        IFluxNetwork network = FluxNetworkCache.INSTANCE.getNetwork(networkID);
        if (network.isValid()) {
            if(network.getMemberPermission(player).canEdit()) {
                network.setSetting(NetworkSettings.NETWORK_WIRELESS, wireless);
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_GENERAL));
                return new FeedbackPacket(EnumFeedbackInfo.SUCCESS);
            } else {
                return new FeedbackPacket(EnumFeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }
}
