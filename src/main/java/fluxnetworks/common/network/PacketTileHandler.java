package fluxnetworks.common.network;

import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.FluxNetworkData;
import fluxnetworks.common.connection.FluxNetworkServer;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTileHandler {

    public static NBTTagCompound getSetNetworkPacket(int id, String password) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(FluxNetworkData.NETWORK_ID, id);
        tag.setString(FluxNetworkData.NETWORK_PASSWORD, password);
        return tag;
    }

    public static IMessage handleSetNetworkPacket(TileFluxCore tile, EntityPlayer player, NBTTagCompound tag) {
        int id = tag.getInteger(FluxNetworkData.NETWORK_ID);
        String pass = tag.getString(FluxNetworkData.NETWORK_PASSWORD);
        if(tile.getNetworkID() == id) {
            return null;
        }
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(id);
        if(network != null) {
            if(tile.getConnectionType().isController() && ((FluxNetworkServer) network).getConnections(FluxType.controller).size() > 0) {
                return new PacketFeedback.FeedbackMessage(FeedbackInfo.HAS_CONTROLLER);
            }
            if(!network.getMemberPermission(player).canAccess()) {
                if(pass.isEmpty()) {
                    return new PacketFeedback.FeedbackMessage(FeedbackInfo.PASSWORD_REQUIRE);
                }
                if (!pass.equals(network.getSetting(NetworkSettings.NETWORK_PASSWORD))) {
                    return new PacketFeedback.FeedbackMessage(FeedbackInfo.REJECT);
                }
            }
            if(tile.getNetwork() != null && !tile.getNetwork().isInvalid()) {
                tile.getNetwork().queueConnectionRemoval(tile, false);
            }
            tile.playerUUID = EntityPlayer.getUUID(player.getGameProfile());
            network.queueConnectionAddition(tile);
            return new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS);
        }
        return null;
    }
}
