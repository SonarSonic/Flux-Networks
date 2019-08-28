package fluxnetworks.common.network;

import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.FluxNetworkData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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

        if(FluxNetworkCache.instance.hasSpaceLeft(player)) {
            FluxNetworkCache.instance.createdNetwork(player, name, color, security, energy, password);
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
            } else {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.NO_OWNER);
            }
        }
        return null;
    }
}
