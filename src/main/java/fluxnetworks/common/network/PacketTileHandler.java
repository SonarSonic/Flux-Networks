package fluxnetworks.common.network;

import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.FluxNetworkServer;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTileHandler {

    public static NBTTagCompound getSetNetworkPacket(int id, String password) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("networkID", id);
        tag.setString("password", password);
        return tag;
    }

    public static IMessage handleSetNetworkPacket(TileFluxCore tile, EntityPlayer player, NBTTagCompound tag) {
        int id = tag.getInteger("networkID");
        String pass = tag.getString("password");
        if(tile.getNetworkID() == id) {
            return null;
        }
        IFluxNetwork network = FluxNetworkCache.instance.getNetwork(id);
        if(network != null) {
            if(tile.getConnectionType().isController() && ((FluxNetworkServer) network).getConnections(IFluxConnector.ConnectionType.CONTROLLER).size() > 0) {
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
            network.queueConnectionAddition(tile);
            return new PacketFeedback.FeedbackMessage(FeedbackInfo.SUCCESS);
        }
        return null;
    }
}
