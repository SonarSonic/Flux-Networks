package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.energy.EnergyType;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.AccessType;
import sonar.flux.api.FluxError;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.connection.NetworkSettings;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class PacketGeneralHelper {

    public static void sendPacketToServer(PacketGeneralType type, NBTTagCompound packetTag) {
        FluxNetworks.network.sendToServer(new PacketGeneral(type, packetTag));
    }

    //// EDIT NETWORK \\\\

    public static NBTTagCompound createNetworkEditPacket(int networkID, String networkName, CustomColour networkColour, AccessType accessType, boolean enableConvert, EnergyType defaultEnergy) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        tag.setString(FluxNetworkData.NETWORK_NAME, networkName);
        tag.setInteger(FluxNetworkData.COLOUR, networkColour.getRGB());
        tag.setInteger(FluxNetworkData.ACCESS, accessType.ordinal());
        tag.setBoolean(FluxNetworkData.CONVERSION, enableConvert);
        EnergyType.writeToNBT(defaultEnergy, tag, FluxNetworkData.ENERGY_TYPE);
        return tag;
    }

    public static IMessage doNetworkEditPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        String newName = packetTag.getString(FluxNetworkData.NETWORK_NAME);
        CustomColour colour = new CustomColour(packetTag.getInteger(FluxNetworkData.COLOUR));
        AccessType access = AccessType.values()[packetTag.getInteger(FluxNetworkData.ACCESS)];
        boolean enableConvert = packetTag.getBoolean(FluxNetworkData.CONVERSION);
        EnergyType energyType = EnergyType.readFromNBT(packetTag, FluxNetworkData.ENERGY_TYPE);

        IFluxNetwork common = FluxNetworks.getServerCache().getNetwork(networkID);
        if (!common.isFakeNetwork()) {
            if (common.getPlayerAccess(player).canEdit()) {
                common.setSetting(NetworkSettings.NETWORK_NAME, newName);
                common.setSetting(NetworkSettings.NETWORK_ACCESS, access);
                common.setSetting(NetworkSettings.NETWORK_COLOUR, colour);
                common.setSetting(NetworkSettings.NETWORK_CONVERSION, enableConvert);
                common.setSetting(NetworkSettings.NETWORK_ENERGY_TYPE, energyType);
            } else {
                return new PacketError(FluxError.EDIT_NETWORK);
            }
        }
        return null;
    }

    //// CREATE NETWORK \\\\

    public static NBTTagCompound createNetworkCreationPacket(String networkName, CustomColour networkColour, AccessType accessType, boolean enableConvert, EnergyType defaultEnergy) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(FluxNetworkData.NETWORK_NAME, networkName);
        tag.setInteger(FluxNetworkData.COLOUR, networkColour.getRGB());
        tag.setInteger(FluxNetworkData.ACCESS, accessType.ordinal());
        tag.setBoolean(FluxNetworkData.CONVERSION, enableConvert);
        EnergyType.writeToNBT(defaultEnergy, tag, FluxNetworkData.ENERGY_TYPE);
        return tag;
    }

    public static IMessage doNetworkCreationPacket(EntityPlayer player, NBTTagCompound packetTag) {
        String newName = packetTag.getString(FluxNetworkData.NETWORK_NAME);
        CustomColour colour = new CustomColour(packetTag.getInteger(FluxNetworkData.COLOUR));
        AccessType access = AccessType.values()[packetTag.getInteger(FluxNetworkData.ACCESS)];
        boolean enableConvert = packetTag.getBoolean(FluxNetworkData.CONVERSION);
        EnergyType energyType = EnergyType.readFromNBT(packetTag, FluxNetworkData.ENERGY_TYPE);

        if (FluxNetworks.getServerCache().hasSpaceForNetwork(player)) {
            FluxNetworks.getServerCache().createNetwork(player, newName, colour, access, enableConvert, energyType);
        }
        return null;
    }

    //// DELETE NETWORK \\\\

    public static NBTTagCompound createNetworkDeletePacket(int networkID) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        return tag;
    }

    public static IMessage doNetworkDeletePacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        IFluxNetwork toDelete = FluxNetworks.getServerCache().getNetwork(networkID);
        if (!toDelete.isFakeNetwork()) {
            if (toDelete.getPlayerAccess(player).canDelete()) {
                FluxNetworks.getServerCache().onPlayerRemoveNetwork(toDelete);
            } else {
                return new PacketError(FluxError.NOT_OWNER);
            }
        }
        return null;
    }

    //// ADD PLAYER \\\\

    public static NBTTagCompound createAddPlayerPacket(int networkID, String playerName, PlayerAccess playerAccess) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("networkID", networkID);
        tag.setString("playerName", playerName);
        tag.setInteger("playerAccess", playerAccess.ordinal());
        return tag;
    }

    public static IMessage doAddPlayerPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger("networkID");
        String playerName = packetTag.getString("playerName");
        PlayerAccess access = PlayerAccess.values()[packetTag.getInteger("playerAccess")];
        IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
        if (!network.isFakeNetwork()) {
            if (network.getPlayerAccess(player).canEdit()) {
                network.addPlayerAccess(playerName, access);
            } else {
                return new PacketError(FluxError.EDIT_NETWORK);
            }
        }
        return null;
    }

    //// REMOVE PLAYER \\\\

    public static NBTTagCompound createRemovePlayerPacket(int networkID, UUID playerRemoved, PlayerAccess playerAccess) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("networkID", networkID);
        tag.setUniqueId("playerRemoved", playerRemoved);
        tag.setInteger("playerAccess", playerAccess.ordinal());
        return tag;
    }

    public static IMessage doRemovePlayerPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger("networkID");
        UUID playerRemoved = packetTag.getUniqueId("playerRemoved");
        PlayerAccess access = PlayerAccess.values()[packetTag.getInteger("playerAccess")];
        IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
        if (!network.isFakeNetwork()) {
            if (network.getPlayerAccess(player).canEdit()) {
                network.removePlayerAccess(playerRemoved, access);
            } else {
                return new PacketError(FluxError.EDIT_NETWORK);
            }
        }
        return null;
    }

    //// CHANGE PLAYER \\\\

    public static NBTTagCompound createChangePlayerPacket(int networkID, UUID playerChanged, PlayerAccess playerAccess) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("networkID", networkID);
        tag.setUniqueId("playerChanged", playerChanged);
        tag.setInteger("playerAccess", playerAccess.ordinal());
        return tag;
    }

    public static IMessage doChangePlayerPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger("networkID");
        UUID playerChanged = packetTag.getUniqueId("playerChanged");
        if (playerChanged != null) {
            if (FluxPlayer.getOnlineUUID(player).equals(playerChanged)) {
                //don't allow editing of their own permissions.
                return null;
            }
            PlayerAccess access = PlayerAccess.values()[packetTag.getInteger("playerAccess")];
            access = SonarHelper.incrementEnum(access, PlayerAccess.values());
            while (access.canDelete()) {
                access = SonarHelper.incrementEnum(access, PlayerAccess.values());
            }

            IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
            if (!network.isFakeNetwork()) {
                if (network.getPlayerAccess(player).canEdit()) {
                    Optional<FluxPlayer> settings = network.getValidFluxPlayer(playerChanged);
                    if (settings.isPresent()) {
                        settings.get().setAccess(access);
                        network.getSyncSetting(NetworkSettings.NETWORK_PLAYERS).setDirty(true);
                    } else {
                        return new PacketError(FluxError.INVALID_USER);
                    }

                } else {
                    return new PacketError(FluxError.EDIT_NETWORK);
                }
            }
        }
        return null;
    }

    //// RESET_CONNECTED_BLOCKS \\\\


    public static NBTTagCompound createResetConnectedBlocksPacket(int networkID) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        return tag;
    }

    public static IMessage doResetConnectedBlocksPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
        if (!network.isFakeNetwork()) {
            network.debugConnectedBlocks();
        }
        return null;
    }

    //// VALIDATE FLUX CONNECTIONS \\\\

    public static NBTTagCompound createValidateConnectionsPacket(int networkID) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        return tag;
    }

    public static IMessage doValidateConnectionsPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
        if (!network.isFakeNetwork()) {
            network.debugValidateFluxConnections();
        }
        return null;
    }

    //// TAB CHANGE \\\\

    public static NBTTagCompound createStateChangePacket(EnumGuiTab oldTab, @Nullable EnumGuiTab newTab) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("oldTab", oldTab.ordinal());
        if(newTab != null) {
            tag.setInteger("newTab", newTab.ordinal());
        }
        return tag;
    }

    public static IMessage doStateChangePacket(EntityPlayer player, NBTTagCompound packetTag) {
        ItemStack stack = player.getHeldItem(player.getActiveHand());
        if(stack.getItem() instanceof IFluxItemGui) {
            EnumGuiTab oldTab = EnumGuiTab.values()[packetTag.getInteger("oldTab")];
            ListenerHelper.onPlayerCloseItemTab(stack, player, oldTab);
            if (packetTag.hasKey("newTab")) {
                EnumGuiTab newTab = EnumGuiTab.values()[packetTag.getInteger("newTab")];
                ListenerHelper.onPlayerOpenItemTab(stack, player, newTab);
            }
        }
        return null;
    }

    //// TAB CHANGE \\\\

    public static NBTTagCompound createViewingNetworkPacket(int networkID) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
        return tag;
    }

    public static IMessage doViewingNetworkPacket(EntityPlayer player, NBTTagCompound packetTag) {
        int networkID = packetTag.getInteger(FluxNetworkData.NETWORK_ID);
        IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
        if (!network.isFakeNetwork() && network.getPlayerAccess(player).canView()) {
            ///WHAT'S HERE!
        }
        return null;
    }


}
