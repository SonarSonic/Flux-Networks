package sonar.fluxnetworks.common.network;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

@Deprecated
public class GeneralPacketHandler {

    /*public static CompoundNBT getCreateNetworkPacket(String name, int color, SecurityType security, EnergyType energy, String password) {
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
        SecurityType security = SecurityType.values()[nbtTag.getInt(FluxNetworkData.SECURITY_TYPE)];
        EnergyType energy = EnergyType.values()[nbtTag.getInt(FluxNetworkData.ENERGY_TYPE)];
        String password = nbtTag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if (!FluxUtils.checkPassword(password)) {
            NetworkHandler.INSTANCE.sendToPlayer(new SFeedbackMessage(EnumFeedbackInfo.ILLEGAL_PASSWORD), player);
            return null;
        }
        if (FluxNetworkData.get().createNetwork(player, name, color, security, password) != null) {
            NetworkHandler.INSTANCE.sendToPlayer(new SFeedbackMessage(EnumFeedbackInfo.SUCCESS), player);
            return null;
        }
        NetworkHandler.INSTANCE.sendToPlayer(new SFeedbackMessage(EnumFeedbackInfo.NO_SPACE), player);
        return null;
    }*/

    /*public static CompoundNBT getNetworkEditPacket(int networkID, String networkName, int color, NetworkSecurity.Type security, EnergyType energy, String password) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxConstants.NETWORK_ID, networkID);
        tag.putString(FluxConstants.NETWORK_NAME, networkName);
        tag.putInt(FluxConstants.NETWORK_COLOR, color);
        tag.putInt(FluxNetworkData.SECURITY_TYPE, security.ordinal());
        tag.putInt(FluxNetworkData.ENERGY_TYPE, energy.ordinal());
        tag.putString(FluxNetworkData.NETWORK_PASSWORD, password);
        return tag;
    }

    public static Object handleNetworkEditPacket(PlayerEntity player, CompoundNBT tag) {
        int networkID = tag.getInt(FluxConstants.NETWORK_ID);
        String newName = tag.getString(FluxConstants.NETWORK_NAME);
        int color = tag.getInt(FluxConstants.NETWORK_COLOR);
        NetworkSecurity.Type security = NetworkSecurity.Type.values()[tag.getInt(FluxNetworkData.SECURITY_TYPE)];
        EnergyType energy = EnergyType.values()[tag.getInt(FluxNetworkData.ENERGY_TYPE)];
        String password = tag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if (!FluxUtils.checkPassword(password)) {
            return new SFeedbackMessage(EnumFeedbackInfo.ILLEGAL_PASSWORD);
        }
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (network.isValid()) {
            if (network.getPlayerAccess(player).canEdit()) {
                boolean needPacket = false;
                if (!network.getNetworkName().equals(newName)) {
                    network.setNetworkName(newName);
                    needPacket = true;
                }
                if (network.getNetworkColor() != color) {
                    network.setNetworkColor(color);
                    needPacket = true;
                    List<IFluxDevice> list = network.getConnections(FluxLogicType.ANY);
                    list.forEach(device -> device.onConnect(network)); // update color data
                }
                if (needPacket) {
                    HashMap<Integer, Tuple<Integer, String>> cache = new HashMap<>();
                    cache.put(networkID, new Tuple<>(network.getNetworkColor() | 0xff000000, network.getNetworkName()));
                    PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new NetworkColourPacket(cache));
                }
                *//*network.setSetting(NetworkSettings.NETWORK_SECURITY, security);
                network.setSetting(NetworkSettings.NETWORK_ENERGY, energy);
                network.setSetting(NetworkSettings.NETWORK_PASSWORD, password);*//*
                PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SNetworkUpdateMessage(Lists.newArrayList(network), FluxConstants.FLAG_NET_BASIS));
                return new SFeedbackMessage(EnumFeedbackInfo.SUCCESS_2);
            } else {
                return new SFeedbackMessage(EnumFeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }*/

    /*public static CompoundNBT getDeleteNetworkPacket(int networkID) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxConstants.NETWORK_ID, networkID);
        return tag;
    }

    public static Object handleDeleteNetworkPacket(PlayerEntity player, CompoundNBT nbtTag) {
        int id = nbtTag.getInt(FluxConstants.NETWORK_ID);
        IFluxNetwork toDelete = FluxNetworkData.getNetwork(id);
        if (toDelete.isValid()) {
            if (toDelete.getPlayerAccess(player).canDelete()) {
                FluxNetworkData.get().deleteNetwork(toDelete);
                return new SFeedbackMessage(EnumFeedbackInfo.SUCCESS);
            } else {
                return new SFeedbackMessage(EnumFeedbackInfo.NO_OWNER);
            }
        }
        return null;
    }*/

    /*@Deprecated
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

        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (network.isValid()) {
            if (network.getAccessPermission(player).canEdit()) {
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

        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (network.isValid()) {
            if (network.getAccessPermission(player).canEdit()) {
                network.removeMember(playerRemoved);
                return new NetworkUpdatePacket(Lists.newArrayList(network), NBTType.NETWORK_PLAYERS);
            } else {
                return new FeedbackPacket(EnumFeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }*/

    /*public static CompoundNBT getChangePermissionPacket(int networkID, UUID playerChanged, int type) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxConstants.NETWORK_ID, networkID);
        tag.putUniqueId("playerChanged", playerChanged);
        tag.putInt("t", type);
        return tag;
    }

    public static Object handleChangePermissionPacket(PlayerEntity player, CompoundNBT packetTag) {
        int networkID = packetTag.getInt(FluxConstants.NETWORK_ID);
        UUID playerChanged = packetTag.getUniqueId("playerChanged");
        int type = packetTag.getInt("t");
        if (playerChanged != null) {
            *//*if (PlayerEntity.getUUID(player.getGameProfile()).equals(playerChanged)) {
                //don't allow editing of their own permissions...
                return null;
            }*//*

            IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
            if (network.isValid()) {
                if (network.getPlayerAccess(player).canEdit()) {
                    // Create new member
                    if (type == 0) {
                        PlayerEntity player1 = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerChanged);
                        //noinspection
                        if (player1 != null) {
                            NetworkMember newMember = NetworkMember.create(player1, AccessLevel.USER);
                            network.getMemberList().add(newMember);
                            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SFeedbackMessage(EnumFeedbackInfo.SUCCESS));
                            return new SNetworkUpdateMessage(Lists.newArrayList(network), 0);
                        }
                        return new SFeedbackMessage(EnumFeedbackInfo.INVALID_USER);
                    } else {
                        Optional<NetworkMember> settings = network.getMemberByUUID(playerChanged);
                        if (settings.isPresent()) {
                            NetworkMember p = settings.get();
                            if (type == 1) {
                                p.setAccessPermission(AccessLevel.ADMIN);
                            } else if (type == 2) {
                                p.setAccessPermission(AccessLevel.USER);
                            } else if (type == 3) {
                                network.getMemberList().remove(p);
                            } else if (type == 4) {
                                *//*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*//*
                                //network.getNetworkMembers().removeIf(f -> f.getAccessPermission().canDelete());
                                //TODO
                                //network.setSetting(NetworkSettings.NETWORK_OWNER, playerChanged);
                                p.setAccessPermission(AccessLevel.OWNER);
                            }
                            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SFeedbackMessage(EnumFeedbackInfo.SUCCESS));
                            return new SNetworkUpdateMessage(Lists.newArrayList(network), 0*//*NBTType.NETWORK_PLAYERS*//*);
                        } else if (type == 4) {
                            PlayerEntity player1 = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerChanged);
                            //noinspection
                            if (player1 != null) {
                                *//*network.getSetting(NetworkSettings.NETWORK_PLAYERS).stream()
                                        .filter(f -> f.getAccessPermission().canDelete()).findFirst().ifPresent(s -> s.setAccessPermission(AccessPermission.USER));*//*
                                //TODO
                                *//*network.getSetting(NetworkSettings.NETWORK_PLAYERS).removeIf(f -> f.getAccessPermission().canDelete());
                                NetworkMember newMember = NetworkMember.createNetworkMember(player1, AccessType.OWNER);
                                network.getSetting(NetworkSettings.NETWORK_PLAYERS).add(newMember);
                                network.setSetting(NetworkSettings.NETWORK_OWNER, playerChanged);*//*
                                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SFeedbackMessage(EnumFeedbackInfo.SUCCESS));
                                return new SNetworkUpdateMessage(Lists.newArrayList(network), 0*//*NBTType.NETWORK_PLAYERS*//*);
                            }
                            return new SFeedbackMessage(EnumFeedbackInfo.INVALID_USER);
                        }
                        return new SFeedbackMessage(EnumFeedbackInfo.INVALID_USER);
                    }
                } else {
                    return new SFeedbackMessage(EnumFeedbackInfo.NO_ADMIN);
                }
            }
        }
        return null;
    }*/

    public static CompoundNBT getChangeWirelessPacket(int networkID, int wirelessMode) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(FluxConstants.NETWORK_ID, networkID);
        tag.putInt(FluxNetworkData.WIRELESS_MODE, wirelessMode);
        return tag;
    }

    public static Object handleChangeWirelessPacket(PlayerEntity player, CompoundNBT packetTag) {
        int networkID = packetTag.getInt(FluxConstants.NETWORK_ID);
        int wireless = packetTag.getInt(FluxNetworkData.WIRELESS_MODE);
        IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
        if (network.isValid()) {
            if (network.getPlayerAccess(player).canEdit()) {
                //network.setSetting(NetworkSettings.NETWORK_WIRELESS, wireless);
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SNetworkUpdateMessage(Lists.newArrayList(network), FluxConstants.TYPE_NET_BASIC));
                return new SFeedbackMessage(EnumFeedbackInfo.SUCCESS);
            } else {
                return new SFeedbackMessage(EnumFeedbackInfo.NO_ADMIN);
            }
        }
        return null;
    }
}
